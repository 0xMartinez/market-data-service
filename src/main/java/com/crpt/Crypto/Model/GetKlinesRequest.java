package com.crpt.Crypto.Model;

import lombok.Value;

@Value
public class GetKlinesRequest {

    String symbol;
    String interval;
    Integer limit;
}
