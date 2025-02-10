package com.crpt.Crypto.Repository.Model;

import com.crpt.Crypto.utill.DatabaseLogger;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.persistence.EntityListeners;
import lombok.*;
import org.springframework.data.cassandra.core.mapping.Column;
import org.springframework.data.cassandra.core.mapping.PrimaryKey;
import org.springframework.data.cassandra.core.mapping.Table;

import java.math.BigDecimal;
import java.time.Instant;

@EntityListeners(DatabaseLogger.class)
@JsonIgnoreProperties(ignoreUnknown = true)
@Table("candlesticks")
@Data
public class CandlestickDb {

    @PrimaryKey
    @JsonProperty("key")
    private CandlestickKey key;
    @Column("open_time")
    private Instant openTime;
    @Column("open_price")
    private BigDecimal openPrice;
    @Column("close_price")
    private BigDecimal closePrice;
    @Column("highest_price")
    private BigDecimal highestPrice;
    @Column("lowest_price")
    private BigDecimal lowestPrice;
    private BigDecimal volume;
    @Column("hlc3_8_indicator")
    private BigDecimal hlc3indicator8;
    @Column("hlc3_21_indicator")
    private BigDecimal hlc3indicator21;
    @Column("signal")
    private Boolean signal = null;

    public CandlestickDb(CandlestickKey key, Instant openTime, BigDecimal closePrice, BigDecimal openPrice, BigDecimal highestPrice, BigDecimal hlc3indicator8, BigDecimal volume, BigDecimal lowestPrice, Boolean signal, BigDecimal hlc3indicator21) {
        this.key = key;
        this.openTime = openTime;
        this.closePrice = closePrice;
        this.openPrice = openPrice;
        this.highestPrice = highestPrice;
        this.hlc3indicator8 = hlc3indicator8;
        this.volume = volume;
        this.lowestPrice = lowestPrice;
        this.signal = signal;
        this.hlc3indicator21 = hlc3indicator21;
    }


    public CandlestickDb() {}


}
