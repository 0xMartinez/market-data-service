package com.crpt.Crypto.Client;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;
import java.util.Map;

@FeignClient(value = "BinanceClient", url = "https://fapi.binance.com")
public interface BinanceClient {

    @GetMapping("/fapi/v1/time")
    Map<String, Long> getServerTime();

    @GetMapping("/fapi/v1/klines")
    List<List<Object>> getKlines(@RequestParam("symbol") String symbol,
                                 @RequestParam("interval") String interval,
                                 @RequestParam(value = "limit", required = false) Integer limit);

    @GetMapping("/fapi/v3/positionRisk")
    List<Object> getPositionInfo(Long timestamp);

}
