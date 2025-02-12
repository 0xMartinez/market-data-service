package com.crpt.Crypto.Model;


import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class BinanceFuturesPositionData {

    private String symbol;
    private String positionAmt;
    private String entryPrice;
    private String breakEvenPrice;
    private String markPrice;
    private String unRealizedProfit;
    private String liquidationPrice;
    private String leverage;
    private String maxNotionalValue;
    private String marginType;
    private String isolatedMargin;
    private Boolean isAutoAddMargin;
    private String positionSide;
    private String notional;
    private String isolatedWallet;
    private long updateTime;
    private Boolean isolated;
    private int adlQuantile;
}

