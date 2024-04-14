package com.stc.xprt.me.validator.controller;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import java.util.List;

@RequestMapping("/stc/xprt")
@RestController
@FieldDefaults(level = AccessLevel.PRIVATE)
@RequiredArgsConstructor
public class HealthCheckController {
    final RestTemplate restTemplate = new RestTemplate();

    static List<String> rpcUrlList = List.of(
            "https://persistence-testnet-rpc.cosmonautstakes.com/",
            "https://persistence-rpc.polkachu.com/",
            "https://rpc.core.persistence.one/",
            "https://persistence-rpc.quantnode.tech/",
            "https://rpc-persistence.architectnodes.com/",
            "https://persistence-rpc.bluestake.net/",
            "https://persistence-rpc.zenscape.one/");

    public String getAliveRPC() {
        for(var rpcUrl : rpcUrlList) {
            UriComponentsBuilder urlBuilder = UriComponentsBuilder.fromHttpUrl(rpcUrl).path("health");
            var response = restTemplate.getForObject(urlBuilder.toUriString(), Object.class);
            if(response != null) {
                return rpcUrl;
            }
        }
        return "";
    }
}
