package com.share.service;
import com.amazonaws.SdkClientException;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.PutObjectRequest;
import com.amazonaws.services.s3.model.S3Object;
import com.amazonaws.services.s3.model.S3ObjectInputStream;
import com.share.model.enums.AWSDirectory;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.poi.util.IOUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import javax.imageio.IIOImage;
import javax.imageio.ImageIO;
import javax.imageio.ImageWriteParam;
import javax.imageio.ImageWriter;
import javax.imageio.stream.ImageOutputStream;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.*;
import java.util.Iterator;
import java.util.Objects;
import java.util.UUID;

@RequiredArgsConstructor
@Service
@Slf4j
public class S3StorageService {

    @Value("${application.bucket.name}")
    private String bucketName;
    @Value("${application.bucket.url}")
    private String baseUrl;

    private final AmazonS3 s3Client;

    private static final long MAX_FILE_SIZE_BYTES = 500 * 1024;
    private static final float INITIAL_QUALITY = 0.7f;
    private static final float QUALITY_STEP = 0.05f;
    private static final float MIN_QUALITY = 0.1f;


    public String uploadFile(MultipartFile file, AWSDirectory awsDirectory) throws IOException {
        MultipartFile multipartFile = zipFile(file);
        File convertedFile = convertMultipartFileToFile(multipartFile);

        String filename = UUID.randomUUID() + ".png";
        s3Client.putObject(new PutObjectRequest(bucketName, awsDirectory.path + filename, convertedFile));

        boolean isDeleted = convertedFile.delete();
        if (!isDeleted) {
            log.warn(String.format("%s silinemedi", file.getOriginalFilename()));
        }

        return baseUrl + awsDirectory.path + filename;
    }

    public String uploadFile(File file, AWSDirectory awsDirectory) {

        String filename = UUID.randomUUID() + ".png";

        s3Client.putObject(new PutObjectRequest(bucketName, awsDirectory.path + filename, file));

        boolean isDeleted = file.delete();
        if (!isDeleted) {
            log.warn(String.format("%s silinemedi", file.getName()));
        }

        return baseUrl + awsDirectory.path + filename;

    }

    public byte[] downloadFile(String filename) {
        S3Object s3Object = s3Client.getObject(bucketName, filename);
        S3ObjectInputStream inputStream = s3Object.getObjectContent();

        try {
            return IOUtils.toByteArray(inputStream);
        } catch (IOException e) {
            log.error("Error converting to byte[] ", e);
        }
        return new byte[0];
    }

    public void deleteFile(String filename) {

        try {
            s3Client.deleteObject(bucketName, filename.substring(baseUrl.length()));
        } catch (SdkClientException e) {
            log.error("Error deleting file from s3 {}", filename, e);
        }

    }


    public File convertMultipartFileToFile(MultipartFile file) throws IOException {
        File convertedFile = new File(Objects.requireNonNull(file.getOriginalFilename()));

        try (FileOutputStream fos = new FileOutputStream(convertedFile)) {
            fos.write(file.getBytes());
        } catch (IOException e) {
            log.error("Error converting multipartFile to file", e);
            throw new IOException(e);
        }

        return convertedFile;
    }


    public MultipartFile zipFile(MultipartFile file) throws IOException {
        // 1. Boyut Kontrolü (Küçükse Sıkıştırma Yapma)
        if (file.getSize() < MAX_FILE_SIZE_BYTES) {
            return file;
        }

        // Görüntü okuma
        BufferedImage inputImage = ImageIO.read(file.getInputStream());
        if (inputImage == null) {
            throw new IllegalArgumentException("Invalid image file format or cannot be read.");
        }

        // 2. Görüntüyü Yeniden Boyutlandır (İlk Sıkıştırma Adımı)
        int width = inputImage.getWidth() / 2;
        int height = inputImage.getHeight() / 2;
        BufferedImage outputImage = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
        Graphics2D g2d = outputImage.createGraphics();
        g2d.drawImage(inputImage, 0, 0, width, height, null);
        g2d.dispose();

        // 3. ImageWriter Hazırlığı
        Iterator<ImageWriter> writers = ImageIO.getImageWritersByFormatName("jpg");
        if (!writers.hasNext()) {
            throw new UnsupportedOperationException("JPEG writer not available on this system.");
        }
        ImageWriter writer = writers.next();

        // writer'ı kullanmak için ByteArrayOutputStream ve ImageOutputStream oluşturulur
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        ImageOutputStream ios = ImageIO.createImageOutputStream(baos);
        writer.setOutput(ios);

        // 4. Sıkıştırma Parametrelerini Ayarlama
        ImageWriteParam param = writer.getDefaultWriteParam();
        if (param.canWriteCompressed()) {
            param.setCompressionMode(ImageWriteParam.MODE_EXPLICIT);
            param.setCompressionQuality(INITIAL_QUALITY);
        }

        byte[] imageData;
        float currentQuality = INITIAL_QUALITY;

        try {
            // 5. Kalite Kontrol Döngüsü
            while (true) {
                baos.reset(); // Önceki denemenin verisini temizle

                // Kalite ayarını yap
                param.setCompressionQuality(currentQuality);

                // Resmi sıkıştırıp hafızaya yaz
                writer.write(null, new IIOImage(outputImage, null, null), param);

                imageData = baos.toByteArray();

                // Boyut kontrolü
                if (imageData.length <= MAX_FILE_SIZE_BYTES) {
                    break; // Boyut hedefimize ulaştık, döngüyü kır.
                }

                // Kaliteyi düşür ve tekrar dene
                currentQuality -= QUALITY_STEP;

                // Eğer minimum kaliteye ulaşıldıysa ve hala büyükse hata fırlat
                if (currentQuality <= MIN_QUALITY) {
                    throw new IOException("Cannot reduce image size below " + (MAX_FILE_SIZE_BYTES / 1024) + " KB even at minimum quality (" + MIN_QUALITY + ").");
                }
            }
        } finally {
            // 6. Kaynakları Serbest Bırak (Döngüden sonra)
            writer.dispose(); // <-- Hatanın düzeltildiği yer
            ios.close();
            baos.close();
        }

        // 7. Yeni MultipartFile oluştur
        return new CustomMultipartFile(imageData, file.getOriginalFilename(), "image/jpeg");
    }

    /**
     * JPEG sıkıştırma işlemi bittikten sonra oluşan byte dizisinden
     * MultipartFile arayüzünü uygulayan özel bir sınıf.
     */
    private static class CustomMultipartFile implements MultipartFile {
        private final byte[] bytes;
        private final String originalFilename;
        private final String contentType;

        public CustomMultipartFile(byte[] bytes, String originalFilename, String contentType) {
            this.bytes = bytes;
            this.originalFilename = originalFilename;
            this.contentType = contentType;
        }

        @Override
        public String getName() { return "file"; }

        @Override
        public String getOriginalFilename() { return originalFilename; }

        @Override
        public String getContentType() { return contentType; }

        @Override
        public boolean isEmpty() { return bytes == null || bytes.length == 0; }

        @Override
        public long getSize() { return bytes.length; }

        @Override
        public byte[] getBytes() { return bytes; }

        @Override
        public InputStream getInputStream() { return new ByteArrayInputStream(bytes); }

        @Override
        public void transferTo(java.io.File dest) throws IOException, IllegalStateException {
            new java.io.FileOutputStream(dest).write(bytes);
        }
    }

}
