package com.stc.router.controller;

import com.stc.side.controller.dto.RestLatestBlockDTO;
import com.stc.side.controller.dto.RestLatestBlockDTO.LatestBlock;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.apache.commons.lang3.math.NumberUtils;
import org.springframework.beans.factory.annotation.Value;
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

@RequestMapping("/stc/router")
@RestController
@FieldDefaults(level = AccessLevel.PRIVATE)
@RequiredArgsConstructor
public class RouterBlockController {
    static Map<String, Object> blockDetailCacheMap = new ConcurrentHashMap<>();
    final RestTemplate restTemplate;

    @Value("${indexer.rest.router}")
    String routerUrl;

    public Long getLatestBlockHeight() {
        UriComponentsBuilder urlBuilder = UriComponentsBuilder.fromHttpUrl(routerUrl).path("/cosmos/base/tendermint/v1beta1/blocks/latest");

        return Optional.ofNullable(restTemplate.getForObject(urlBuilder.toUriString(), RestLatestBlockDTO.class))
                       .map(RestLatestBlockDTO::getBlock).map(LatestBlock::getHeader)
                       .map(RestLatestBlockDTO.BlockHeader::getHeight).map(Long::valueOf).orElse(Long.MIN_VALUE);
    }

    @GetMapping("/block/last")
    public Object getLatestBlockDetail() {
        String latestHeight = String.valueOf(this.getLatestBlockHeight());

        if (blockDetailCacheMap.containsKey(latestHeight)) {
            return blockDetailCacheMap.get(latestHeight);
        }

        var latestBlockDetail = getBlockDetail(String.valueOf(latestHeight));
        blockDetailCacheMap.clear();
        blockDetailCacheMap.put(latestHeight, latestBlockDetail);
        return latestBlockDetail;
    }

    @GetMapping("/block/{height}")
    public Object getBlockDetail(@PathVariable String height) {
        if (!NumberUtils.isParsable(height)) {
            return "Invalid block height";
        }

        if (blockDetailCacheMap.containsKey(height)) {
            return blockDetailCacheMap.get(height);
        }

        UriComponentsBuilder blockDetailUrl = UriComponentsBuilder.fromHttpUrl(routerUrl).path("/cosmos/base/tendermint/v1beta1/blocks/").path(height);

        Object result = Optional.ofNullable(restTemplate.getForObject(blockDetailUrl.toUriString(), Map.class))
                                .orElse(Map.of()).getOrDefault("block", Map.of());
        blockDetailCacheMap.put(height, result);
        return result;
    }

    @GetMapping("/blocks")
    Object getBlockList() {
        List result = new ArrayList();
        if (blockDetailCacheMap.size() >= 6) {
            return blockDetailCacheMap.values().stream().limit(6).toList();
        }
        Long latestHeight = this.getLatestBlockHeight();
        while (result.size() < 6) {
            long previousHeight = latestHeight - result.size();
            if (blockDetailCacheMap.containsKey(String.valueOf(previousHeight))) {
                result.add(blockDetailCacheMap.get(String.valueOf(previousHeight)));
                continue;
            }
            var block = this.getBlockDetail(String.valueOf(previousHeight));
            result.add(block);
            blockDetailCacheMap.put(String.valueOf(previousHeight), block);
        }
        return result;
    }

    @Scheduled(fixedDelay = 300000)
    public void clearBlockDetailCache() {
        blockDetailCacheMap.clear();
        System.out.println(" Block detail cache cleared.");
    }
}
