package com.crpt.Crypto.Model;


import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;
import java.util.Date;

@Builder
public class Candlestick {
    private String symbol;
    private Date closeTime;
    private Date openTime;
    private BigDecimal openPrice;
    private BigDecimal closePrice;
    private BigDecimal highestPrice;
    private BigDecimal lowestPrice;
    private BigDecimal volume;
    private BigDecimal currentPrice;
}