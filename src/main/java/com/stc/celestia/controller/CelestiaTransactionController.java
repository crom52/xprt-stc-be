package com.stc.celestia.controller;


import com.stc.celestia.dto.CelestiaTransactionResponse;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.math.NumberUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.util.CollectionUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

import static org.apache.commons.lang3.math.NumberUtils.isParsable;

@RequestMapping("/stc/celestia")
@RestController
@FieldDefaults(level = AccessLevel.PRIVATE)
@RequiredArgsConstructor
public class CelestiaTransactionController {
    static Map<String, Object> txsCacheMap = new ConcurrentHashMap();
    final RestTemplate restTemplate;
    @Value("${indexer.rest.celestia}")
    String xprtUrl;

    @GetMapping("tx/list")
    public CelestiaTransactionResponse getTransactions(@RequestParam(value = "num", required = false, defaultValue = "10") Integer num) {
        UriComponentsBuilder urlBuilder = UriComponentsBuilder.fromHttpUrl(xprtUrl).path("/api/v1/txs")
                                                              .queryParam("limit", num);

        CelestiaTransactionResponse response = restTemplate.getForObject(urlBuilder.toUriString(),
                                                                         CelestiaTransactionResponse.class);
        if (response == null || response.getData().isEmpty()) {
            return null;
        }

        for (var txs : response.getData()) {
            String cacheKey = txs.getHeight() + "-" + txs.getHash();
            txsCacheMap.put(cacheKey, List.of(txs));
        }
        return response;
    }

    @GetMapping("tx/{heightOrHash}")
    public Object getTransactionBy(@PathVariable String heightOrHash) {
        List result = this.findTxsInCache(heightOrHash);

        if (CollectionUtils.isEmpty(result)) {
            Optional.ofNullable(getTransactions(999)).map(CelestiaTransactionResponse::getData).orElse(List.of());
            result = this.findTxsInCache(heightOrHash);
        }
        return result;
    }

    private List<Object> findTxsInCache(String heightOrHash) {
        if(heightOrHash == null) {
            return null;
        }
        List<Object> res = new ArrayList<>();
        txsCacheMap.forEach((key, value) -> {
            if (key.contains(heightOrHash)) {
                res.add(value);
            }
        });
        return res;
    }


    @Scheduled(fixedDelay = 600000)
    public void clearCache() {
        txsCacheMap.clear();
        System.out.println("Transaction Cache cleared.");
    }
}
