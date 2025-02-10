package com.crpt.Crypto.utill;

import jakarta.persistence.PrePersist;
import jakarta.persistence.PreRemove;
import jakarta.persistence.PreUpdate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

@Component
public class DatabaseLogger {

    private static final Logger logger = LoggerFactory.getLogger(DatabaseLogger.class);

    @PrePersist
    public void logInsert(Object entity) {
        logger.info("Dodano nową encję: {}", entity);
    }

    @PreUpdate
    public void logUpdate(Object entity) {
        logger.info("Zaktualizowano encję: {}", entity);
    }

    @PreRemove
    public void logDelete(Object entity) {
        logger.info("Usunięto encję: {}", entity);
    }
}