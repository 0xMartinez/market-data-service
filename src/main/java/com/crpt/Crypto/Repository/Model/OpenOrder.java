package com.crpt.Crypto.Repository.Model;


import lombok.Builder;
import lombok.Getter;
import org.springframework.data.cassandra.core.mapping.PrimaryKey;
import org.springframework.data.cassandra.core.mapping.Table;

import java.math.BigDecimal;

@Table("open_orders")
@Getter
@Builder
public class OpenOrder {

    @PrimaryKey
    private OpenOrderKey key;
    private BigDecimal positionAmt;
    private BigDecimal entryPrice;
    private BigDecimal markPrice;
    private BigDecimal unRealizedProfit;
    private BigDecimal liquidationPrice;
    private BigDecimal leverage;
    private String positionSide;

}
