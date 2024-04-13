package com.stc.xprt.me.validator.controller;

import com.stc.xprt.me.validator.ABCIInfo;
import com.stc.xprt.me.validator.ABCIInfoResult;
import com.stc.xprt.me.validator.ABCIInfoResultResponse;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import java.util.Map;
import java.util.Optional;

@RequestMapping("/stc/xprt")
@RestController
@FieldDefaults(level = AccessLevel.PRIVATE)
@RequiredArgsConstructor
public class BlockController {
    final RestTemplate restTemplate = new RestTemplate();
    @Value("${indexer.api.url}")
    String xprtUrl;

    public Long getLatestBlockHeight() {
        UriComponentsBuilder urlBuilder = UriComponentsBuilder.fromHttpUrl(xprtUrl).path("abci_info");

        return Optional.ofNullable(restTemplate.getForObject(urlBuilder.toUriString(), ABCIInfo.class))
                       .map(ABCIInfo::getResult).map(ABCIInfoResult::getResponse)
                       .map(ABCIInfoResultResponse::getLastBlockHeight).orElse(Long.MIN_VALUE);
    }

    @GetMapping("/block/last")
    public Object getLatestBlockDetail() {
        Long latestHeight = this.getLatestBlockHeight();
        return getBlockDetail(String.valueOf(latestHeight));
    }

    @GetMapping("/block/{heightOrHash}")
    public Object getBlockDetail(@PathVariable String heightOrHash) {
        UriComponentsBuilder blockDetailUrl = UriComponentsBuilder.fromHttpUrl(xprtUrl);
        if(heightOrHash.startsWith("0x")) {
            blockDetailUrl.path("block_by_hash").queryParam("hash", "0x" + heightOrHash);
        } else {
            blockDetailUrl.path("block").queryParam("height", heightOrHash);
        }

        var result = Optional.ofNullable(restTemplate.getForObject(blockDetailUrl.toUriString(), Map.class))
                        .orElse(Map.of());
        return result.getOrDefault("result", Map.of());
    }

}
