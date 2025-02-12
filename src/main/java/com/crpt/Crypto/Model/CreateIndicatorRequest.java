package com.crpt.Crypto.Model;

import lombok.Value;

@Value
public class CreateIndicatorRequest {

    IndicatorType indicatorType;
    Source source;
    Integer periodFast;
    Integer periodMedium;
    Integer periodSlow;

}
