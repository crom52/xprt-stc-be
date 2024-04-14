package com.stc.xprt.me.validator.controller;


import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.GetMapping;
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
public class TransactionController {
    final RestTemplate restTemplate = new RestTemplate();
    @Value("${indexer.api.url}")
    String xprtUrl;

    @GetMapping("/net_info")
    public Map getNetInfo() {
        UriComponentsBuilder urlBuilder = UriComponentsBuilder.fromHttpUrl(xprtUrl + "/net_info");

        return Optional.ofNullable(restTemplate.getForObject(urlBuilder.toUriString(), Map.class)).orElseGet(Map::of);
    }
}
