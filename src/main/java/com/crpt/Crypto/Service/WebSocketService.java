package com.crpt.Crypto.Service;


import com.crpt.Crypto.Model.GetKlinesRequest;
import com.crpt.Crypto.Repository.CandlestickRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class WebSocketService {
    private final CandlestickRepository candlestickRepository;
    private final BinanceService binanceService;

   // private final WebSocketStreamHandler webSocketStreamHandler;

    public void createAndStreamFutureCandlesticks(final GetKlinesRequest request) {

        //binanceService.getKlines(request);
        //binanceWebSocketClient.connect(String.format("btcusdt@kline_%s", request.getInterval()));
        //webSocketStreamHandler.startWebSocketProcessing();
    }
}
    /*
    public void backtest() {
        BarSeries series = new BaseBarSeriesBuilder().withName("series").build();

        candlestickRepository.findAll().forEach(candlestick -> {
            final BaseBar bar = BaseBar.builder(DecimalNum::valueOf, Number.class)
                    .timePeriod(Duration.ofMinutes(15))
                    .endTime(candlestick.getKey().getCloseTime().atZone(ZoneId.of("Europe/Warsaw")))
                    .openPrice(candlestick.getOpenPrice())
                    .highPrice(candlestick.getHighestPrice())
                    .lowPrice(candlestick.getLowestPrice())
                    .closePrice(candlestick.getClosePrice())
                    .volume(candlestick.getVolume())
                    .build();
            series.addBar(bar);
        });
        ClosePriceIndicator closePriceIndicator = new ClosePriceIndicator(series);
        RSIIndicator rsiIndicator = new RSIIndicator(closePriceIndicator, 14);

        ZLEMAIndicator zlemaIndicator8 = new ZLEMAIndicator(closePriceIndicator, 8);
        ZLEMAIndicator zlemaIndicator21 = new ZLEMAIndicator(closePriceIndicator, 21);
        ZLEMAIndicator zlemaIndicator55 = new ZLEMAIndicator(closePriceIndicator, 55);

        Rule entryRule = new CrossedUpIndicatorRule(zlemaIndicator8, zlemaIndicator21)
                .and(new UnderIndicatorRule(rsiIndicator, 40)).and(new UnderIndicatorRule(closePriceIndicator, zlemaIndicator55));
        Rule exitRule = new CrossedDownIndicatorRule(zlemaIndicator8, zlemaIndicator21).and(new OverIndicatorRule(rsiIndicator, 60));

        Strategy myStrategy = new BaseStrategy(entryRule, exitRule);
        BarSeriesManager seriesManager = new BarSeriesManager(series);



        TradingRecord tradingRecord = seriesManager.run(createMACDStrategy(series));
        CashFlow cashFlow = new CashFlow(series, tradingRecord);
        Num finalValue = cashFlow.getValue(series.getEndIndex());
        Num initialValue = DecimalNum.valueOf(0);
        System.out.println(finalValue.minus(initialValue));
        System.out.println(tradingRecord);
    }

    private static Strategy createMACDStrategy(BarSeries series) {
        ClosePriceIndicator closePrice = new ClosePriceIndicator(series);
        MACDIndicator macd = new MACDIndicator(closePrice, 12, 26);
        EMAIndicator macdSignal = new EMAIndicator(macd, 9);

        // Reguły kupna i sprzedaży
        Rule buyingRule = new CrossedUpIndicatorRule(macd, macdSignal); // MACD przecina sygnał od dołu
        Rule sellingRule = new CrossedDownIndicatorRule(macd, macdSignal); // MACD przecina sygnał od góry

        return new BaseStrategy("MACD Strategy", buyingRule, sellingRule);
    }
}

     */


