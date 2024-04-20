package com.stc.xprt.me.validator.controller;

import com.stc.xprt.me.validator.ABCIInfo;
import com.stc.xprt.me.validator.ABCIInfoResult;
import com.stc.xprt.me.validator.ABCIInfoResultResponse;
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

import javax.annotation.PostConstruct;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

@RequestMapping("/stc/xprt")
@RestController
@FieldDefaults(level = AccessLevel.PRIVATE)
@RequiredArgsConstructor
public class BlockController {
    final RestTemplate restTemplate = new RestTemplate();
    final HealthCheckController healthCheckController;
    //    @Value("${indexer.api.url}")
    //    String xprtUrl;
    static Map<Long, Object> lastBlockCacheMap = new ConcurrentHashMap<>();
    static Map<String, Object> blockDetailCacheMap = new ConcurrentHashMap();


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
        if(lastBlockCacheMap.containsKey(latestHeight)) {
            return lastBlockCacheMap.get(latestHeight);
        }

        var latestBlockDetail =  getBlockDetail(String.valueOf(latestHeight));
        lastBlockCacheMap.put(latestHeight, latestBlockDetail);
        return latestBlockDetail;
    }

    @GetMapping("/block/{heightOrHash}")
    public Object getBlockDetail(@PathVariable String heightOrHash) {
        String xprtUrl = healthCheckController.getAliveRPC();
        if (xprtUrl.isBlank()) {
            throw new RuntimeException("There is no RPC endpoint alive");
        }
        if(blockDetailCacheMap.containsKey(heightOrHash)) {
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
        while(result.size() < 6) {
            var block = this.getBlockDetail(String.valueOf(latestHeight - result.size()));
            result.add(block);
        }
        return result;
    }

    @Scheduled(fixedDelay = 900000)
    public void clearCache() {
        lastBlockCacheMap.clear();
        blockDetailCacheMap.clear();
        System.out.println("Block cache cleared.");
    }
}
