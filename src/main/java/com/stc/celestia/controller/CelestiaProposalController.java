package com.stc.celestia.controller;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@RestController(value = "CelestiaProposalController")
@RequestMapping("/stc/celestia")
@FieldDefaults(level = AccessLevel.PRIVATE)
@RequiredArgsConstructor
public class CelestiaProposalController {
    @Value("${indexer.rest.celestia}")
    String celestiaTestnetUrl;

    final RestTemplate restTemplate;
    static Map<String, Object> proposalsCacheMap = new ConcurrentHashMap<>();

    @GetMapping("/proposals")
    public Object getProposals() {
        String cacheKey = "proposals";
        if(proposalsCacheMap.containsKey(cacheKey)) {
            return proposalsCacheMap.get(cacheKey);
        }

        UriComponentsBuilder urlBuilder = UriComponentsBuilder.fromHttpUrl(celestiaTestnetUrl).path("/api/v1/proposals");
        var proposals= restTemplate.getForObject(urlBuilder.toUriString(), Object.class);
        proposalsCacheMap.put(cacheKey, proposals);
        return proposals;
    }

    @Scheduled(fixedDelay = 300000)
    public void clearProposalCache() {
        proposalsCacheMap.clear();
        System.out.println("Proposals cache cleared.");
    }
}
