package com.crpt.Crypto.Repository.Model;

import lombok.Builder;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;
import org.springframework.data.cassandra.core.mapping.PrimaryKeyClass;
import org.springframework.data.cassandra.core.mapping.PrimaryKeyColumn;

import java.io.Serializable;
import java.time.Instant;

@PrimaryKeyClass
@Setter
@Getter
@Data
public class CandlestickKey implements Serializable {
    @PrimaryKeyColumn(name = "symbol", ordinal = 0, type = org.springframework.data.cassandra.core.cql.PrimaryKeyType.PARTITIONED)
    private String symbol;

    @PrimaryKeyColumn(name = "close_time", ordinal = 1, type = org.springframework.data.cassandra.core.cql.PrimaryKeyType.CLUSTERED)
    private Instant closeTime;

    public CandlestickKey() {
    }

    public CandlestickKey(String symbol, Instant closeTime) {
        this.symbol = symbol;
        this.closeTime = closeTime;
    }
}