package com.crpt.Crypto.Model;

import lombok.Builder;
import lombok.Data;

@Builder
@Data
public class WebsocketRequest {

    private String id;
    private String method;
    private Params params;

    @Builder
    @Data
    public static class Params {
        private String apiKey;
        private String quantity;
        private String recvWindow;
        private String side;
        private String signature;
        private String symbol;
        private String timeInForce;
        private String timestamp;
        private String type;
    }
}
