package com.crpt.Crypto.utill;


import com.binance.connector.futures.client.impl.UMFuturesClientImpl;
import com.crpt.Crypto.Client.BinanceClient;
import com.crpt.Crypto.Model.BinanceCandlestickStreamResponse;
import com.crpt.Crypto.Model.BinanceFuturesPositionData;
import com.crpt.Crypto.Model.CreateOrderRequest;
import com.crpt.Crypto.Repository.Model.OpenOrder;
import com.crpt.Crypto.Repository.Model.OpenOrderKey;
import com.crpt.Crypto.Repository.Model.OpenOrderRepository;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.*;

@Service
@RequiredArgsConstructor
@Slf4j
public class BinanceUtil {

    private final static String API_KEY = "";
    private final static String SECRET_KEY = "";

    private final BinanceClient binanceClient;
    private final OpenOrderRepository openOrderRepository;


    public BinanceCandlestickStreamResponse translateResponseToModel(final String message) throws JsonProcessingException {

        final ObjectMapper mapper = new ObjectMapper();
        final JsonNode rootNode = mapper.readTree(message);
        final JsonNode klineData = rootNode.get("k");

        return BinanceCandlestickStreamResponse.builder()
                .kline(BinanceCandlestickStreamResponse.Kline.builder()
                        .symbol(klineData.get("s").asText())
                        .klineStartTime(new Date(Long.parseLong(klineData.get("t").asText())))
                        .klineCloseTime(new Date(Long.parseLong(klineData.get("T").asText())))
                        .openPrice(BigDecimal.valueOf(klineData.get("o").asDouble()))
                        .highPrice(BigDecimal.valueOf(klineData.get("h").asDouble()))
                        .lowPrice(BigDecimal.valueOf(klineData.get("l").asDouble()))
                        .closePrice(BigDecimal.valueOf(klineData.get("c").asDouble()))
                        .baseAssetVolume(BigDecimal.valueOf(klineData.get("v").asDouble()))
                        .isKlineClosed(klineData.get("x").asBoolean())
                        .build())
                .build();
    }

    public String createOrder(final CreateOrderRequest request) throws JsonProcessingException {

        UMFuturesClientImpl client = new UMFuturesClientImpl(API_KEY, SECRET_KEY, "https://fapi.binance.com");
        LinkedHashMap<String, Object> parameters = new LinkedHashMap<>();
        double leverage = request.getLeverage();
        double percentageValue = request.getPercentageValue();

        final Double quantity = BigDecimal.valueOf(((((percentageValue / 100.0) * 300.0) / 101380.0) * leverage) * 0.956).setScale(3, RoundingMode.DOWN).doubleValue();
        parameters.put("symbol", request.getSymbol());
        parameters.put("side", request.getSide());
        parameters.put("type", request.getType());
        parameters.put("quantity", quantity);
        parameters.put("positionSide", request.getPositionSide());

        log.info("new {} order for {} created", request.getPositionSide(), request.getSymbol());
        final String response = client.account().newOrder(parameters);

        final BinanceFuturesPositionData positionData = getPositionInfo(request.getSymbol()).stream()
                .filter(position -> request.getPositionSide().equals(position.getPositionSide())).findFirst().orElse(null);

        if (positionData != null) {
            openOrderRepository.save(OpenOrder.builder()
                    .key(OpenOrderKey.builder()
                            .symbol(positionData.getSymbol())
                            .timestamp(new Date(getServerTimestamp()).toInstant())
                            .build())
                    .entryPrice(BigDecimal.valueOf(Double.parseDouble(positionData.getEntryPrice())))
                    .markPrice(BigDecimal.valueOf(Double.parseDouble(positionData.getMarkPrice())))
                    .positionAmt(BigDecimal.valueOf(Double.parseDouble(positionData.getPositionAmt())))
                    .liquidationPrice(BigDecimal.valueOf(Double.parseDouble(positionData.getLiquidationPrice())))
                    .unRealizedProfit(BigDecimal.valueOf(Double.parseDouble(positionData.getUnRealizedProfit())))
                    .leverage(BigDecimal.valueOf(Double.parseDouble(positionData.getLeverage())))
                    .positionSide(positionData.getPositionSide())
                    .build());
        } else {
            log.info("Something went wrong during fetching position data");
        }

        return response;
    }

    public List<BinanceFuturesPositionData> returnPositionListPerSymbol(final String symbol) throws JsonProcessingException {
        return getPositionInfo(symbol);
    }
    private List<BinanceFuturesPositionData> getPositionInfo(final String symbol) throws JsonProcessingException {

        LinkedHashMap<String, Object> parameters = new LinkedHashMap<>();
        parameters.put("symbol", symbol);
        UMFuturesClientImpl client = new UMFuturesClientImpl(API_KEY, SECRET_KEY, "https://fapi.binance.com");
        String response = client.account().positionInformation(parameters);

        ObjectMapper objectMapper = new ObjectMapper();

        return objectMapper.readValue(response, new TypeReference<List<BinanceFuturesPositionData>>() {});
    }

    public Long getServerTimestamp() {
        return binanceClient.getServerTime().get("serverTime");
    }
}
