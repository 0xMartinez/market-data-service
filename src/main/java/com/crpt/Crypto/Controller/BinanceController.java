package com.crpt.Crypto.Controller;

import com.crpt.Crypto.Model.CreateIndicatorRequest;
import com.crpt.Crypto.Model.GetKlinesRequest;
import com.crpt.Crypto.Repository.Model.CandlestickDb;
import com.crpt.Crypto.Service.BinanceService;
import com.crpt.Crypto.Service.CandlestickService;
import com.fasterxml.jackson.core.JsonProcessingException;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RequiredArgsConstructor
@RestController
@RequestMapping("/market/data")
public class BinanceController {

    final private BinanceService binanceService;
    final private CandlestickService candlestickService;

    @PostMapping
    public void getCandlesticks(@RequestBody final GetKlinesRequest request) {
        binanceService.getKlines(null, request.getSymbol(), request.getInterval(), request.getLimit());
    }

    @PostMapping("{symbol}/{interval}/{limit}")
    public void connect(@RequestBody final CreateIndicatorRequest request, @PathVariable("symbol") final String symbol, @PathVariable("interval") final String interval, @PathVariable("limit") final Integer limit) {
        binanceService.connect(request, symbol, interval, limit);
    }

    @PostMapping("/cache")
    public List<CandlestickDb> getCache() throws JsonProcessingException {
        return candlestickService.getAllCandlesticks();
    }
}
