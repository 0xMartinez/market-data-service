package com.crpt.Crypto.Model;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class CreateOrderRequest {

    private String symbol;
    private String side;
    private String type;
    private String positionSide;
    private Double percentageValue;
    private Double leverage;
}
