package com.share.service.schedular;

import com.share.repository.SharingRepository;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@RequiredArgsConstructor
@Component
public class SharingSchedular {
    // todo expire aşan paylaşımlar silinecek
    private static final Logger log = LoggerFactory.getLogger(SharingSchedular.class);

    private final SharingRepository sharingRepository;

    @Transactional
    @Scheduled(cron = "0 * * * * *")
    public void cleanupExpiredShares() {
        log.info("Zaman aşımına uğramış paylaşımları temizleme görevi başlatıldı.");

        // Şu andan 24 saat öncesinin zamanını hesapla
        LocalDateTime twentyFourHoursAgo = LocalDateTime.now().minusHours(24);

        try {
            // Repository metodunu çağırarak eski kayıtları sil ve silinen kayıdın sayısını al
            int deletedCount = sharingRepository.deleteByExpireStartBefore(twentyFourHoursAgo);

            if (deletedCount > 0) {
                log.info("{} adet zaman aşımına uğramış paylaşım silindi.", deletedCount);
            } else {
                log.info("Silinecek zaman aşımına uğramış paylaşım bulunamadı.");
            }
        } catch (Exception e) {
            log.error("Paylaşımları temizleme görevi sırasında bir hata oluştu.", e);
        }
    }
}
