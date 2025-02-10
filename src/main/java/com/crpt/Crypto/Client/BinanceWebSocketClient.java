package com.crpt.Crypto.Client;

import com.crpt.Crypto.Model.*;
import com.crpt.Crypto.Repository.CandlestickRepository;
import com.crpt.Crypto.Repository.Model.CandlestickDb;
import com.crpt.Crypto.Repository.Model.CandlestickKey;
import com.crpt.Crypto.Repository.Model.OpenOrderRepository;
import com.crpt.Crypto.Service.CandlestickService;
import jakarta.websocket.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.ta4j.core.*;
import org.ta4j.core.indicators.ZLEMAIndicator;
import org.ta4j.core.num.DecimalNum;
import org.ta4j.core.rules.CrossedDownIndicatorRule;
import org.ta4j.core.rules.CrossedUpIndicatorRule;

import java.math.RoundingMode;
import java.net.URI;
import java.time.Duration;
import java.time.ZoneId;

@Component
@ClientEndpoint
@RequiredArgsConstructor
@Slf4j
public class BinanceWebSocketClient {

    private final static String SIDE_BUY = "BUY";
    private final static String SIDE_SELL = "SELL";
    private final static String POSITION_SIDE_LONG = "LONG";
    private final static String POSITION_SIDE_SHORT = "SHORT";
    private final static String MARKET_TYPE = "MARKET";

    private Session session;
    private final CandlestickRepository candlestickRepository;
    private final OpenOrderRepository openOrderRepository;
    private final BinanceUtil binanceUtil;
    private final CandlestickService candlestickService;

    public void connect(String stream) {
        String uri = "wss://fstream.binance.com/ws/" + stream;
        try {
            final WebSocketContainer container = ContainerProvider.getWebSocketContainer();
            container.connectToServer(this, new URI(uri));
            System.out.println("Połączono z Binance WebSocket: " + uri);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @OnOpen
    public void onOpen(Session session) {
        this.session = session;
        System.out.println("Połączenie otwarte z Binance.");
    }

    @OnMessage
    public void onMessage(String message) throws Exception {

        final BinanceCandlestickStreamResponse.Kline mappedKline = binanceUtil.translateResponseToModel(message).getKline();
        final String symbol = mappedKline.getSymbol();
        final boolean isKlineClosed = mappedKline.getIsKlineClosed();
        final BarSeries barSeries = new BaseBarSeriesBuilder().withName("barSeries").build();

        candlestickService.getCacheCandlesticks();
        candlestickService.getAllCandlesticks().forEach(candlestickDb -> {
            barSeries.addBar(BaseBar.builder(DecimalNum::valueOf, Number.class)
                    .timePeriod(Duration.ofMinutes(1))
                    .endTime(candlestickDb.getKey().getCloseTime().atZone(ZoneId.of("Europe/Warsaw")))
                    .openPrice(candlestickDb.getOpenPrice())
                    .highPrice(candlestickDb.getHighestPrice())
                    .lowPrice(candlestickDb.getLowestPrice())
                    .closePrice(candlestickDb.getClosePrice())
                    .volume(candlestickDb.getVolume())
                    .build());
        });


        BaseBar barTmp = null;
        final Bar currentBar = barSeries.getBar(barSeries.getEndIndex());
        if (currentBar.getEndTime().equals(mappedKline.getKlineCloseTime().toInstant().atZone(ZoneId.of("Europe/Warsaw")))) {

            currentBar.addPrice(DecimalNum.valueOf(mappedKline.getClosePrice()));
        } else {
            barTmp = BaseBar.builder(DecimalNum::valueOf, Number.class)
                    .timePeriod(Duration.ofMinutes(15))
                    .endTime(mappedKline.getKlineCloseTime().toInstant().atZone(ZoneId.of("Europe/Warsaw")))
                    .openPrice(mappedKline.getOpenPrice().doubleValue())
                    .highPrice(mappedKline.getHighPrice().doubleValue())
                    .lowPrice(mappedKline.getLowPrice().doubleValue())
                    .closePrice(mappedKline.getClosePrice().doubleValue())
                    .volume(mappedKline.getBaseAssetVolume().doubleValue())
                    .build();
            barSeries.addBar(barTmp);
        }

        final HLC3Indicator hlc3Indicator = new HLC3Indicator(barSeries);
        final ZLEMAIndicator zlemaIndicator8 = new ZLEMAIndicator(hlc3Indicator, 8);
        final ZLEMAIndicator zlemaIndicator21 = new ZLEMAIndicator(hlc3Indicator, 21);


        final Rule entryRule = new CrossedUpIndicatorRule(zlemaIndicator8, zlemaIndicator21);
        final Rule exitRule = new CrossedDownIndicatorRule(zlemaIndicator8, zlemaIndicator21);

        Boolean signal = null;

        if (entryRule.isSatisfied(barSeries.getEndIndex())) {
            signal = true;
        } else if (exitRule.isSatisfied(barSeries.getEndIndex())) {
            signal = false;
        }
        final CandlestickDb candlestickDb = new CandlestickDb();

        CandlestickKey candlestickKey = new CandlestickKey();
        candlestickKey.setSymbol(symbol);
        candlestickKey.setCloseTime(mappedKline.getKlineCloseTime().toInstant());

        candlestickDb.setKey(candlestickKey);

        candlestickDb.setOpenPrice(barTmp != null ? barTmp.getOpenPrice().bigDecimalValue() : currentBar.getOpenPrice().bigDecimalValue());
        candlestickDb.setClosePrice(barTmp != null ? barTmp.getClosePrice().bigDecimalValue() : currentBar.getClosePrice().bigDecimalValue());
        candlestickDb.setHighestPrice(barTmp != null ? barTmp.getHighPrice().bigDecimalValue() : currentBar.getHighPrice().bigDecimalValue());
        candlestickDb.setLowestPrice(barTmp != null ? barTmp.getLowPrice().bigDecimalValue() : currentBar.getLowPrice().bigDecimalValue());
        candlestickDb.setVolume(mappedKline.getBaseAssetVolume());
        candlestickDb.setHlc3indicator8(zlemaIndicator8.getValue(barSeries.getEndIndex()).bigDecimalValue().setScale(1, RoundingMode.DOWN));
        candlestickDb.setHlc3indicator21(zlemaIndicator21.getValue(barSeries.getEndIndex()).bigDecimalValue().setScale(1, RoundingMode.DOWN));
        candlestickDb.setSignal(signal);

        candlestickRepository.save(candlestickDb);
        if (isKlineClosed) {
            candlestickService.saveCandlestick(candlestickDb);

        }

        if (signal != null && !signal
                && openOrderRepository.findFuturesOpenOrdersByTimestampBetweenAndSymbol(candlestickDb.getKey().getCloseTime(), candlestickDb.getOpenTime().minusSeconds(60), symbol).isEmpty()) {
            final CreateOrderRequest request = CreateOrderRequest.builder()
                    .symbol(symbol)
                    .side(SIDE_SELL)
                    .type(MARKET_TYPE)
                    .positionSide(POSITION_SIDE_SHORT)
                    .percentageValue(30.0)
                    .leverage(5.0)
                    .build();

            log.info("{}", request);

        } else if (signal != null && signal
                && openOrderRepository.findFuturesOpenOrdersByTimestampBetweenAndSymbol(candlestickDb.getKey().getCloseTime(), candlestickDb.getOpenTime().minusSeconds(60), symbol).isEmpty()) {
            final CreateOrderRequest request = CreateOrderRequest.builder()
                    .symbol(symbol)
                    .side(SIDE_BUY)
                    .type(MARKET_TYPE)
                    .positionSide(POSITION_SIDE_LONG)
                    .percentageValue(30.0)
                    .leverage(5.0)
                    .build();

        }
    }

    @OnClose
    public void onClose(Session session, CloseReason closeReason) {
        System.out.println("Połączenie zamknięte: " + closeReason.getReasonPhrase());
    }

    @OnError
    public void onError(Session session, Throwable throwable) {
        System.err.println("Błąd WebSocket: " + throwable.getMessage());
    }

    public void disconnect() {
        try {
            if (session != null && session.isOpen()) {
                session.close();
                System.out.println("Połączenie z Binance zostało zamknięte.");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}



