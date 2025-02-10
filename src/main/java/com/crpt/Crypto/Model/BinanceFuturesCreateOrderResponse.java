package com.crpt.Crypto.Model;

import lombok.Value;

import java.math.BigDecimal;

@Value
public class BinanceFuturesCreateOrderResponse {

    String clientOrderId;
    BigDecimal cumQty;
    BigDecimal cumQuote;
    BigDecimal executedQty;
    Long orderId;
    BigDecimal avgPrice;
    BigDecimal origQty;
    BigDecimal price;
    Long reduceOnly;
    String side;
    String positionSide;
    String status;
    BigDecimal stopPrice; // ignore when order type is TRAILING_STOP_MARKET
    Boolean closePosition;
    String symbol;
    String timeInForce;
    String type;
    String origType;
    BigDecimal activatePrice; // activation price for TRAILING_STOP_MARKET
    BigDecimal priceRate; // callback rate for TRAILING_STOP_MARKET
    Long updateTime;
    String workingType;
    Boolean priceProtect;
    String priceMatch; // price match mode
    String selfTradePreventionMode; // self trading prevention mode
    Long goodTillDate; // auto-cancel time for GTD orders
}
