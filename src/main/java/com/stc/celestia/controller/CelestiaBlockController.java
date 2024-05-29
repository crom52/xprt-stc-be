package com.stc.celestia.controller;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
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

import static org.springframework.web.util.UriComponentsBuilder.fromHttpUrl;

@RequestMapping("/stc/celestia")
@RestController(value = "CelestiaBlockController")
@FieldDefaults(level = AccessLevel.PRIVATE)
@RequiredArgsConstructor
public class CelestiaBlockController {
    static Map<Number, Object> lastBlockDetailCacheMap = new ConcurrentHashMap<>();
    static Map<String, Object> blockDetailCacheMap = new ConcurrentHashMap<>();
    List<Number> lastHeightCache = new ArrayList<>();
    final RestTemplate restTemplate;
    @Value("${indexer.rest.celestia}")
    String celestiaUrl;

    @GetMapping("/block/last")
    public Object getLatestBlockDetail() {
        if(!lastHeightCache.isEmpty() && lastBlockDetailCacheMap.containsKey(lastHeightCache.get(0))){
            return lastBlockDetailCacheMap.get(lastHeightCache.get(0));
        }

        UriComponentsBuilder urlBuilder = fromHttpUrl(celestiaUrl).path("/api/v1/blocks").queryParam("limit", 1);

        Map latestBlock = Optional.ofNullable(restTemplate.getForObject(urlBuilder.toUriString(), Map.class))
                                  .orElse(Map.of());
        List<Map> x = (List<Map>) latestBlock.get("data");

        if(CollectionUtils.isEmpty(x)) {
            return Map.of();
        }

        Number latestHeight = (Number) x.get(0).getOrDefault("height", 0L);
        lastHeightCache.add(0, latestHeight);
        lastBlockDetailCacheMap.put(latestHeight, latestBlock);
        return latestBlock;
    }

    @GetMapping("/block/{height}")
    public Object getBlockDetail(@PathVariable String height) {
        //
        if (blockDetailCacheMap.containsKey(height)) {
            return blockDetailCacheMap.get(height);
        }

        if (!NumberUtils.isParsable(height)) {
            return Map.of();
        }

        String url = "https://celestia.explorers.guru/_next/data/yQ3pC3SJWKomq4x6UsfRc/block/";
        Object result = Optional.ofNullable(restTemplate.getForObject(url + height + ".json", Map.class))
                                .orElse(Map.of()).getOrDefault("pageProps", Map.of());
        blockDetailCacheMap.put(height, result);
        return result;
    }

    @GetMapping("/blocks")
    Object getBlockList(@RequestParam(defaultValue = "10", required = false) Integer limit) {

        UriComponentsBuilder urlBuilder = fromHttpUrl(celestiaUrl).path("/api/v1/blocks")
                                                              .queryParam("limit", limit);

        return Optional.ofNullable(restTemplate.getForObject(urlBuilder.toUriString(), Map.class)).orElse(Map.of());
    }

    @Scheduled(fixedDelay = 60000)
    public void clearLastBlockCache() {
        lastBlockDetailCacheMap.clear();
        lastHeightCache.clear();;
        System.out.println("Clear latest block.");
    }

    @Scheduled(fixedDelay = 900000)
    public void clearBlockDetailCache() {
        blockDetailCacheMap.clear();
        System.out.println("Block detail cache cleared.");
    }
}
