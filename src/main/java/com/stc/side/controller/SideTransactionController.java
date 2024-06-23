package com.stc.side.controller;


import com.stc.xprt.controller.BlockController;
import com.stc.xprt.controller.HealthCheckController;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@RequestMapping("/stc/side")
@RestController
@FieldDefaults(level = AccessLevel.PRIVATE)
@RequiredArgsConstructor
public class SideTransactionController {
    static Map txsCacheMap = new ConcurrentHashMap<>();
    static Map decodedTxsCacheMap = new ConcurrentHashMap<>();
    final RestTemplate restTemplate;
    final HealthCheckController healthCheckController;
    final BlockController blockController;


    @GetMapping("tx/list")
    public Object getTransactions(@RequestParam(value = "num", required = false, defaultValue = "10") Integer num,
                                  @RequestParam(value = "offset", required = false, defaultValue = "0") Integer offset,
                                  @RequestParam(value = "height", required = false) Long height) {

        String cacheKey = height.toString() + num.toString() + offset.toString();
        if (txsCacheMap.containsKey(height)) {
            return txsCacheMap.get(cacheKey);
        }

        UriComponentsBuilder urlBuilder = UriComponentsBuilder.fromHttpUrl(
                                                                      "https://side.api.t.stavr.tech/cosmos/tx/v1beta1/txs/block/").path(String.valueOf(height))
                                                              .queryParam("pagination.offset", offset)
                                                              .queryParam("pagination.limit", num)
                                                              .queryParam("pagination.count_total", true)
                                                              .queryParam("pagination.reverse", true);

        Map response = restTemplate.getForObject(urlBuilder.toUriString(), Map.class);
        if (response == null || response.get("block") == null) {
            return null;
        }

        var data = ((Map<String, Object>) response.get("block")).get("data");
        List txs = ((ArrayList) ((Map) data).get("txs"));
        List decodedTxs = new ArrayList();

        if (txs.isEmpty()) {
            ((Map<String, Object>) data).put("decoded_txs", List.of());
            return response;
        }

        for (var decoded : txs) {
            decodedTxs.add(decodeTxs(decoded.toString()));
        }
        ((Map<String, Object>) data).put("decoded_txs", decodedTxs);
        txsCacheMap.put(cacheKey, response);
        return response;
    }

    private Object decodeTxs(String encoded) {
        if (decodedTxsCacheMap.containsKey(encoded)) {
            return decodedTxsCacheMap.get(encoded);
        }
        UriComponentsBuilder urlBuilder = UriComponentsBuilder.fromHttpUrl(
                "https://side.api.t.stavr.tech/cosmos/tx/v1beta1/decode");
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.setAccept(List.of(MediaType.APPLICATION_JSON));
        var body = Map.of("tx_bytes", encoded);
        HttpEntity request = new HttpEntity<>(body, headers);
        var result = restTemplate.postForObject(urlBuilder.toUriString(), request, Map.class);
        decodedTxsCacheMap.put(encoded, result);
        return result;
    }

    @Scheduled(fixedDelay = 900000)
    public void clearCache() {
        decodedTxsCacheMap.clear();
        txsCacheMap.clear();
        System.out.println("Transaction Cache cleared.");
    }
}
