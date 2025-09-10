package org.atmgigi.hyobankingbe.txn.service;

import lombok.RequiredArgsConstructor;
import org.atmgigi.hyobankingbe.txn.repository.IdempotencyKeyRepository;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

@Component
@RequiredArgsConstructor
public class IdemCleanupJob {
    private final IdempotencyKeyRepository repo;

    @Scheduled(cron = "0 0/5 * * * *") // 5분마다
    public void purge() {
        repo.deleteExpired(LocalDateTime.now());
    }
}