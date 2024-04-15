package com.stc.xprt.me.validator.controller;


import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.util.Base64Utils;
import org.springframework.util.MultiValueMap;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

@RequestMapping("/stc/xprt")
@RestController
@FieldDefaults(level = AccessLevel.PRIVATE)
@RequiredArgsConstructor
public class TransactionController {
    final RestTemplate restTemplate = new RestTemplate();
    final HealthCheckController healthCheckController;
    final BlockController blockController;
    //    @Value("${indexer.api.url}")
    //    String xprtUrl;

    @Value("${indexer.rest.url}")
    String xprtUrl;

    @GetMapping("/net_info")
    public Map getNetInfo() {
        String xprtUrl = healthCheckController.getAliveRPC();
        if (xprtUrl.isBlank()) {
            throw new RuntimeException("There is no RPC endpoint alive");
        }

        UriComponentsBuilder urlBuilder = UriComponentsBuilder.fromHttpUrl(xprtUrl + "/net_info");

        return Optional.ofNullable(restTemplate.getForObject(urlBuilder.toUriString(), Map.class)).orElseGet(Map::of);
    }

    @GetMapping("tx/list")
    public Object getTransactions(@RequestParam(value = "num", required = false, defaultValue = "10") Integer num,
                                  @RequestParam(value = "offset", required = false, defaultValue = "0") Integer offset,
                                  @RequestParam(value = "height", required = false) Long height) {
        Long latestHeight = blockController.getLatestBlockHeight();

        UriComponentsBuilder urlBuilder = UriComponentsBuilder.fromHttpUrl(xprtUrl)
                                                              .path("/cosmos/tx/v1beta1/txs/block/" + latestHeight)
                                                              .queryParam("pagination.offset", offset)
                                                              .queryParam("pagination.limit", num)
                                                              .queryParam("pagination.count_total", true)
                                                              .queryParam("pagination.reverse", true);

        Map response = restTemplate.getForObject(urlBuilder.toUriString(), Map.class);
        if(response == null || response.get("block") == null) {
            return null;
        }

        var data = ((Map<String, Object>)response.get("block")).get("data");
        List txs = ((ArrayList)((Map)data).get("txs"));
        List decodedTxs = new ArrayList();

        if(txs.isEmpty()) {
            ((Map<String, Object>) data).put("decoded_txs", List.of());
            return response;
        }

        for (var decoded : txs) {
            decodedTxs.add(decodeTxs(decoded.toString()));
        }
        ((Map<String, Object>) data).put("decoded_txs", decodedTxs);
        return  response;
    }

    private Object decodeTxs(String decoded) {
        UriComponentsBuilder urlBuilder = UriComponentsBuilder.fromHttpUrl(xprtUrl).path("/cosmos/tx/v1beta1/decode");
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.setAccept(List.of(MediaType.APPLICATION_JSON));
        var body = Map.of("tx_bytes", decoded);
        HttpEntity request = new HttpEntity<>(body, headers);
        return restTemplate.postForObject(urlBuilder.toUriString(), request, Map.class);
    }
}
