package com.stc.xprt.me.validator.controller;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@RequestMapping("/stc/xprt")
@RestController
@FieldDefaults(level = AccessLevel.PRIVATE)
@RequiredArgsConstructor
public class HealthCheckController {
    static Map aliveRPCMap = new ConcurrentHashMap();
    static List<String> rpcUrlList = List.of("https://rpc.core.persistence.one/",
                                             "https://persistence-testnet-rpc.cosmonautstakes.com/",
                                             "https://persistence-rpc.polkachu.com/",
                                             "https://persistence-rpc.quantnode.tech/",
                                             "https://rpc-persistence.architectnodes.com/",
                                             "https://persistence-rpc.bluestake.net/",
                                             "https://persistence-rpc.zenscape.one/");
    final RestTemplate restTemplate = new RestTemplate();

    public String getAliveRPC() {
        for (var rpcUrl : rpcUrlList) {
            UriComponentsBuilder urlBuilder = UriComponentsBuilder.fromHttpUrl(rpcUrl).path("health");
            var response = restTemplate.getForObject(urlBuilder.toUriString(), Object.class);
            if (response != null) {
                aliveRPCMap.put(response, response);
                return rpcUrl;
            }
        }
        return "";
    }

    @Scheduled(fixedDelay = 900000)
    public void clearCache() {
        aliveRPCMap.clear();
        System.out.println("Healthcheck cache cleared.");
    }
}
