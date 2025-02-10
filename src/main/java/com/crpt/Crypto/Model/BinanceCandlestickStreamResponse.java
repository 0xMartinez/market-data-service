package com.crpt.Crypto.Model;

import lombok.Builder;
import lombok.Getter;

import java.math.BigDecimal;
import java.util.Date;

@Builder
@Getter
public class BinanceCandlestickStreamResponse {

    private EventType eventType;
    private Date eventDate;
    private String symbol;
    private Kline kline;

    @Builder
    @Getter
    public static class Kline {
        private Date klineStartTime;
        private Date klineCloseTime;
        private String symbol;
        private String interval;
        private Integer firstTradeId;
        private Integer lastTradeId;
        private BigDecimal openPrice;
        private BigDecimal closePrice;
        private BigDecimal highPrice;
        private BigDecimal lowPrice;
        private BigDecimal baseAssetVolume;
        private Long numberOfTrades;
        private Boolean isKlineClosed;
    }

    enum EventType {
        kline;
    }
}
