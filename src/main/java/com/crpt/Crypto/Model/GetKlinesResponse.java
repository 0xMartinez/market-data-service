package com.crpt.Crypto.Model;

import lombok.Builder;
import lombok.Getter;

@Builder
@Getter
public class GetKlinesResponse {

    String symbol;
    Interval interval;
}
