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

    String symbol;
    String positionAmt;
    String entryPrice;
    String breakEvenPrice;
    String markPrice;
    String unRealizedProfit;
    String liquidationPrice;
    String leverage;
    String maxNotionalValue;
    String marginType;
    String isolatedMargin;
    Boolean isAutoAddMargin;
    String positionSide;
    String notional;
    String isolatedWallet;
    long updateTime;
    Boolean isolated;
    int adlQuantile;
}

