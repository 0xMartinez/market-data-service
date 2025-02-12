package com.crpt.Crypto.Service;


import com.crpt.Crypto.Client.BinanceClient;
import com.crpt.Crypto.Client.BinanceWebSocketClient;
import com.crpt.Crypto.Configuration.CredentialsConfig;
import com.crpt.Crypto.Model.CreateIndicatorRequest;
import com.crpt.Crypto.Model.IndicatorType;
import com.crpt.Crypto.Model.Source;
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

    public void connect(final CreateIndicatorRequest request, final String symbol, final String interval, final Integer limit) {

        getKlines(request, symbol, interval, limit);
        binanceWebSocketClient.connect(String.format("%s@kline_%s", symbol, interval));
    }

    public void getKlines(final CreateIndicatorRequest request, final String symbol, final String interval, final Integer limit) {

        candlestickRepository.deleteAll();
        candlestickService.deleteCacheCandlesticks();
        final BarSeries series = new BaseBarSeriesBuilder().withName("barSeries").build();

        final List<List<Object>> response = binanceClient.getKlines(symbol, interval, limit);
        for (List<Object> kline : response) {

            final BigDecimal openPrice = BigDecimal.valueOf(Double.parseDouble(kline.get(1).toString()));
            final BigDecimal closePrice = BigDecimal.valueOf(Double.parseDouble(kline.get(4).toString()));
            final BigDecimal highestPrice = BigDecimal.valueOf(Double.parseDouble(kline.get(2).toString()));
            final BigDecimal lowestPrice = BigDecimal.valueOf(Double.parseDouble(kline.get(3).toString()));
            final BigDecimal volume = BigDecimal.valueOf(Double.parseDouble((kline.get(5).toString())));
            final Date openTime = new Date(Long.parseLong(kline.get(0).toString()));
            final Date closeTime = new Date(Long.parseLong(kline.get(6).toString()));

            long differenceMillis = closeTime.getTime() - openTime.getTime();
            if (closeTime.before(Date.from(Instant.now()))) {
                final BaseBar bar = BaseBar.builder(DecimalNum::valueOf, Number.class)
                        .timePeriod(Duration.ofMillis(differenceMillis))
                        .endTime(closeTime.toInstant().atZone(ZoneId.of("Europe/Warsaw")))
                        .openPrice(openPrice)
                        .highPrice(highestPrice)
                        .lowPrice(lowestPrice)
                        .closePrice(closePrice)
                        .volume(volume)
                        .build();

                series.addBar(bar);

                CandlestickDb candlestick = new CandlestickDb();
                CandlestickKey candlestickKey = new CandlestickKey();
                candlestickKey.setSymbol(symbol);
                candlestickKey.setCloseTime(closeTime.toInstant());
                candlestick.setKey(candlestickKey);
                candlestick.setOpenTime(openTime.toInstant());
                candlestick.setOpenPrice(openPrice);
                candlestick.setClosePrice(closePrice);
                candlestick.setHighestPrice(highestPrice);
                candlestick.setLowestPrice(lowestPrice);
                candlestick.setVolume(volume);
                createAndSetIndicator(request, candlestick, series);

                candlestickRepository.save(candlestick);
                candlestickService.saveCandlestick(candlestick);
            }
        }
    }

    private void createAndSetIndicator(final CreateIndicatorRequest request, final CandlestickDb candlestick, final BarSeries series) {

        Indicator<Num> indicator = null;
        if (Source.HLC3 == request.getSource()) {
            indicator = new HLC3Indicator(series);
        }

        if (indicator != null && IndicatorType.ZLEMAIndicator == request.getIndicatorType()) {
            final ZLEMAIndicator zlemaIndicatorFast = new ZLEMAIndicator(indicator, request.getPeriodFast());
            final ZLEMAIndicator zlemaIndicatorMedium = new ZLEMAIndicator(indicator, request.getPeriodMedium());
            final ZLEMAIndicator zlemaIndicatorSlow = new ZLEMAIndicator(indicator, request.getPeriodSlow());

            candlestick.setHlc3indicator8(zlemaIndicatorFast.getValue(series.getEndIndex()).bigDecimalValue().setScale(1, RoundingMode.DOWN));
            candlestick.setHlc3indicator21(zlemaIndicatorMedium.getValue(series.getEndIndex()).bigDecimalValue().setScale(1, RoundingMode.DOWN));
        }
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

