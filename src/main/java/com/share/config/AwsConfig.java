package com.share.config;

import org.apache.http.conn.ssl.NoopHostnameVerifier;
import org.apache.http.conn.ssl.SSLConnectionSocketFactory;
import org.apache.http.conn.ssl.TrustAllStrategy;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.ssl.SSLContexts;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import software.amazon.awssdk.http.apache.ApacheHttpClient;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.s3.S3Client;

import javax.net.ssl.SSLContext;

@Configuration
public class AwsConfig {

    @Bean
    public S3Client s3Client() throws Exception {
        // Trust all SSL context oluştur
        SSLContext sslContext = SSLContexts.custom()
                .loadTrustMaterial(null, new TrustAllStrategy())
                .build();

        // Özel HTTP client oluştur
        CloseableHttpClient httpClient = HttpClients.custom()
                .setSSLContext(sslContext)
                .setSSLHostnameVerifier(NoopHostnameVerifier.INSTANCE)
                .build();

        return  S3Client.builder()
                .region(Region.US_EAST_1)
                .httpClient(ApacheHttpClient.builder()
                        // ApacheHttpClient ayarlarını burada yapabilirsin, örneğin:
                        .maxConnections(50)
                        .build())
                .build();
    }
}