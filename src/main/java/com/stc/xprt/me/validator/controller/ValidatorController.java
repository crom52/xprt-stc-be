package com.stc.xprt.me.validator.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.stc.xprt.me.validator.ValidatorInnerData;
import com.stc.xprt.me.validator.ValidatorsResponse;
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

import java.util.Optional;

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
    @Value("${indexer.api.url}")
    String xprtUrl;

    @GetMapping("/validators")
    public Object getValidators(@RequestParam(defaultValue = "10") String num,
                                @RequestParam(defaultValue = "1") String offset) throws JsonProcessingException {


        Long latestBlock = blockController.getLatestBlockHeight();

        if (latestBlock == null || latestBlock < 0) {
            return ValidatorsResponse.builder().build();
        }

        UriComponentsBuilder urlBuilder = UriComponentsBuilder.fromHttpUrl(xprtUrl + "/validators")
                                                              .queryParam("height", latestBlock)
                                                              .queryParam("per_page", num)
                                                              .queryParam("page", offset);

        var rs = Optional.ofNullable(restTemplate.getForObject(urlBuilder.toUriString(), ValidatorInnerData.class));

        return ValidatorsResponse.builder().data(rs.orElseGet(ValidatorInnerData::new)).build();
    }
}
