package com.crpt.Crypto.Model;

import lombok.Setter;
import lombok.Value;

@Setter
@Value
public class GetKlinesRequest {

    String symbol;
    String interval;
    Integer limit;
}
