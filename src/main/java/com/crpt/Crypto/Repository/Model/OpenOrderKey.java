package com.crpt.Crypto.Repository.Model;


import lombok.Builder;
import lombok.Getter;
import org.springframework.data.cassandra.core.mapping.PrimaryKeyClass;
import org.springframework.data.cassandra.core.mapping.PrimaryKeyColumn;

import java.time.Instant;

@PrimaryKeyClass
@Getter
@Builder
public class OpenOrderKey {

    @PrimaryKeyColumn(name = "symbol", ordinal = 0, type = org.springframework.data.cassandra.core.cql.PrimaryKeyType.PARTITIONED)
    private String symbol;

    @PrimaryKeyColumn(name = "timestamp", ordinal = 1, type = org.springframework.data.cassandra.core.cql.PrimaryKeyType.CLUSTERED)
    private Instant timestamp;

}
