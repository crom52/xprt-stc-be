package com.stc.xprt.me.validator.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.stc.xprt.me.validator.ValidatorInnerData;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

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
    //    @Value("${indexer.api.url}")
    //    String xprtUrl;

    @GetMapping("/validators")
    public ValidatorInnerData getValidators(@RequestParam(defaultValue = "10") String num,
                                            @RequestParam(defaultValue = "1") String offset) throws JsonProcessingException {


        String xprtUrl = healthCheckController.getAliveRPC();
        if (xprtUrl.isBlank()) {
            throw new RuntimeException("There is no RPC endpoint alive");
        }

        Long latestBlock = blockController.getLatestBlockHeight();

        if (latestBlock == null || latestBlock < 0) {
            return ValidatorInnerData.builder().build();
        }

        UriComponentsBuilder urlBuilder = UriComponentsBuilder.fromHttpUrl(xprtUrl + "/validators")
                                                              .queryParam("height", latestBlock)
                                                              .queryParam("per_page", num).queryParam("page", offset);

        return restTemplate.getForObject(urlBuilder.toUriString(), ValidatorInnerData.class);
    }
}
