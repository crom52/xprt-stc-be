package com.stc.xprt.controller;

import com.stc.xprt.dto.ABCIInfo;
import com.stc.xprt.dto.ABCIInfoResult;
import com.stc.xprt.dto.ABCIInfoResultResponse;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.apache.commons.lang3.math.NumberUtils;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

@RequestMapping("/stc/xprt")
@RestController
@FieldDefaults(level = AccessLevel.PRIVATE)
@RequiredArgsConstructor
public class BlockController {
    static Map<Long, Object> lastBlockDetailCacheMap = new ConcurrentHashMap<>();
    static Map<String, Object> blockDetailCacheMap = new ConcurrentHashMap<>();
    static List<Long> latestBlockCacheMap = new ArrayList<>(1);
    final RestTemplate restTemplate;
    final HealthCheckController healthCheckController;

    public Long getLatestBlockHeight() {
        String xprtUrl = healthCheckController.getAliveRPC();
        UriComponentsBuilder urlBuilder = UriComponentsBuilder.fromHttpUrl(xprtUrl).path("abci_info");

        return Optional.ofNullable(restTemplate.getForObject(urlBuilder.toUriString(), ABCIInfo.class))
                       .map(ABCIInfo::getResult).map(ABCIInfoResult::getResponse)
                       .map(ABCIInfoResultResponse::getLastBlockHeight).orElse(Long.MIN_VALUE);
    }

    @GetMapping("/block/last")
    @Cacheable(cacheNames = "getLatestBlockDetail")
    public Object getLatestBlockDetail() {
        Long latestHeight = this.getLatestBlockHeight();

        if (lastBlockDetailCacheMap.containsKey(latestHeight)) {
            return lastBlockDetailCacheMap.get(latestHeight);
        }

        var latestBlockDetail = getBlockDetail(String.valueOf(latestHeight));
        lastBlockDetailCacheMap.put(latestHeight, latestBlockDetail);
        return latestBlockDetail;
    }

    @GetMapping("/block/{heightOrHash}")
    public Object getBlockDetail(@PathVariable String heightOrHash) {
        String xprtUrl = healthCheckController.getAliveRPC();
        if (xprtUrl.isBlank()) {
            throw new RuntimeException("There is no RPC endpoint alive");
        }
        if (blockDetailCacheMap.containsKey(heightOrHash)) {
            return blockDetailCacheMap.get(heightOrHash);
        }

        UriComponentsBuilder blockDetailUrl = UriComponentsBuilder.fromHttpUrl(xprtUrl);
        if (NumberUtils.isParsable(heightOrHash)) {
            blockDetailUrl.path("block").queryParam("height", heightOrHash);
        } else {
            blockDetailUrl.path("block_by_hash").queryParam("hash", "0x" + heightOrHash);
        }

        Object result = Optional.ofNullable(restTemplate.getForObject(blockDetailUrl.toUriString(), Map.class))
                                .orElse(Map.of()).getOrDefault("result", Map.of());
        blockDetailCacheMap.put(heightOrHash, result);
        return result;
    }

    @GetMapping("/blocks")
    Object getBlockList() {
        Long latestHeight = this.getLatestBlockHeight();
        List result = new ArrayList();
        while (result.size() < 6) {
            var block = this.getBlockDetail(String.valueOf(latestHeight - result.size()));
            result.add(block);
        }
        return result;
    }

    @Scheduled(fixedDelay = 900000)
    public void clearBlockCache() {
        lastBlockDetailCacheMap.clear();
        blockDetailCacheMap.clear();
        System.out.println("Block detail it statcache cleared.");
    }

    @Scheduled(fixedDelay = 900000)
    public void clearLastBlockCache() {
        latestBlockCacheMap.clear();
        System.out.println("Latest Block cache cleared.");
    }
}
