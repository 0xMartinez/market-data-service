package com.crpt.Crypto.Service;


import com.crpt.Crypto.Client.BinanceClient;
import com.crpt.Crypto.Client.BinanceWebSocketClient;
import com.crpt.Crypto.Repository.Model.CandlestickDb;
import com.crpt.Crypto.Repository.Model.CandlestickKey;
import com.crpt.Crypto.Model.HLC3Indicator;
import com.crpt.Crypto.Repository.CandlestickRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.ta4j.core.*;
import org.ta4j.core.indicators.AbstractIndicator;
import org.ta4j.core.indicators.ZLEMAIndicator;
import org.ta4j.core.num.DecimalNum;
import org.ta4j.core.num.Num;
import org.ta4j.core.rules.CrossedDownIndicatorRule;
import org.ta4j.core.rules.CrossedUpIndicatorRule;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.Duration;
import java.time.Instant;
import java.time.ZoneId;
import java.util.Date;
import java.util.List;

@Service
@RequiredArgsConstructor
public class BinanceService {

    private final BinanceClient binanceClient;
    private final CandlestickRepository candlestickRepository;
    final private BinanceWebSocketClient binanceWebSocketClient;
    final private CandlestickService candlestickService;


    public void connect(final String symbol, final String interval, final Integer limit) {

        getKlines(symbol, interval, limit);
        binanceWebSocketClient.connect(String.format("%s@kline_%s", symbol, interval));
    }

    public BarSeries getKlines(final String symbol, final String interval, final Integer limit) {

        candlestickRepository.deleteAll();
        candlestickService.deleteCacheCandlesticks();

        System.out.println("all deleted");
        final List<List<Object>> response = binanceClient.getKlines(symbol, interval, limit);
        BarSeries series = new BaseBarSeriesBuilder().withName("historicSeries").build();
        int index = 0;
        for (List<Object> kline : response) {

            final BigDecimal openPrice = BigDecimal.valueOf(Double.parseDouble(kline.get(1).toString()));
            final BigDecimal closePrice = BigDecimal.valueOf(Double.parseDouble(kline.get(4).toString()));
            final BigDecimal highestPrice = BigDecimal.valueOf(Double.parseDouble(kline.get(2).toString()));
            final BigDecimal lowestPrice = BigDecimal.valueOf(Double.parseDouble(kline.get(3).toString()));
            final BigDecimal volume = BigDecimal.valueOf(Double.parseDouble((kline.get(5).toString())));
            final Date time = new Date(Long.parseLong(kline.get(6).toString()));

            if (time.before(Date.from(Instant.now()))) {
                final BaseBar bar = BaseBar.builder(DecimalNum::valueOf, Number.class)
                        .timePeriod(Duration.ofMinutes(30))
                        .endTime(time.toInstant().atZone(ZoneId.of("Europe/Warsaw")))
                        .openPrice(openPrice)
                        .highPrice(highestPrice)
                        .lowPrice(lowestPrice)
                        .closePrice(closePrice)
                        .volume(volume)
                        .build();

                series.addBar(bar);
                HLC3Indicator hlc3Indicator = new HLC3Indicator(series);

                final ZLEMAIndicator zlemaIndicator8 = calculateZlemaIndicator(hlc3Indicator, 8);
                final ZLEMAIndicator zlemaIndicator21 = calculateZlemaIndicator(hlc3Indicator, 21);
                final Rule entryRule = createEntryRule(zlemaIndicator8, zlemaIndicator21);
                final Rule exitRule = createExitRule(zlemaIndicator8, zlemaIndicator21);

                Boolean signal = null;
                if (entryRule.isSatisfied(index)) {
                    signal = true;
                } else if (exitRule.isSatisfied(index)) {
                    signal = false;
                }

                CandlestickDb candlestick = new CandlestickDb();
                CandlestickKey candlestickKey = new CandlestickKey();
                candlestickKey.setSymbol(symbol);
                candlestickKey.setCloseTime(time.toInstant());
                candlestick.setKey(candlestickKey);
                candlestick.setOpenPrice(bar.getOpenPrice().bigDecimalValue());
                candlestick.setClosePrice(bar.getClosePrice().bigDecimalValue());
                candlestick.setHighestPrice(bar.getHighPrice().bigDecimalValue());
                candlestick.setLowestPrice(bar.getLowPrice().bigDecimalValue());
                candlestick.setVolume(bar.getVolume().bigDecimalValue());
                candlestick.setHlc3indicator8(zlemaIndicator8.getValue(series.getEndIndex()).bigDecimalValue().setScale(1, RoundingMode.DOWN));
                candlestick.setHlc3indicator21(zlemaIndicator21.getValue(series.getEndIndex()).bigDecimalValue().setScale(1, RoundingMode.DOWN));
                candlestick.setSignal(signal);

                index++;
                candlestickRepository.save(candlestick);
                candlestickService.saveCandlestick(candlestick);
            }
        }
        return series;
    }

    private Rule createEntryRule(final AbstractIndicator<Num> indicator1, final AbstractIndicator<Num> indicator2) {

        return new CrossedUpIndicatorRule(indicator1, indicator2);
    }

    private Rule createExitRule(final AbstractIndicator<Num> indicator1, final AbstractIndicator<Num> indicator2) {

        return new CrossedDownIndicatorRule(indicator1, indicator2);
    }

    private ZLEMAIndicator calculateZlemaIndicator(final HLC3Indicator hlc3Indicator, final int barCount) {

        return new ZLEMAIndicator(hlc3Indicator, barCount);
    }
}

