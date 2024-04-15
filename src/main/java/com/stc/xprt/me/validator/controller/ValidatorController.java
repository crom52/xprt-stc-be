package com.stc.xprt.me.validator.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.stc.xprt.me.validator.ValidatorInnerData;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import java.util.Map;

import static com.fasterxml.jackson.databind.DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES;
import static com.fasterxml.jackson.databind.PropertyNamingStrategy.SNAKE_CASE;

@RequestMapping("/stc/xprt")
@RestController
@FieldDefaults(level = AccessLevel.PRIVATE)
@RequiredArgsConstructor
public class ValidatorController {
    final RestTemplate restTemplate = new RestTemplate();
    final ObjectMapper objectMapper = new ObjectMapper().setPropertyNamingStrategy(SNAKE_CASE)
                                                        .disable(FAIL_ON_UNKNOWN_PROPERTIES);

    final BlockController blockController;
    final HealthCheckController healthCheckController;

    @Value("${indexer.rest.url}")
    String xprtUrl;

    @GetMapping("/validators")
    public Object getValidators(@RequestParam(defaultValue = "10") String num,
                                @RequestParam(defaultValue = "0") String offset) throws JsonProcessingException {


//        String xprtUrl = healthCheckController.getAliveRPC();
//        if (xprtUrl.isBlank()) {
//            throw new RuntimeException("There is no RPC endpoint alive");
//        }
//
//        Long latestBlock = blockController.getLatestBlockHeight();

//        if (latestBlock == null || latestBlock < 0) {
//            return ValidatorInnerData.builder().build();
//        }

        UriComponentsBuilder urlBuilder = UriComponentsBuilder.fromHttpUrl(xprtUrl + "/cosmos/staking/v1beta1/validators")
//                                                              .queryParam("height", latestBlock)
                                                              .queryParam("pagination.count_total", true)
                                                              .queryParam("pagination.reverse", true)
                                                              .queryParam("pagination.limit", num)
                                                              .queryParam("pagination.offset", offset);

        return restTemplate.getForObject(urlBuilder.toUriString(), Map.class);
    }
}
