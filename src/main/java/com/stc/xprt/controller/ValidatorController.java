package com.stc.xprt.controller;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@RequestMapping("/stc/xprt")
@RestController
@FieldDefaults(level = AccessLevel.PRIVATE)
@RequiredArgsConstructor
public class ValidatorController {
    final RestTemplate restTemplate;

    static Map<String, Object> validatorCacheMap = new ConcurrentHashMap<>();

    @Value("${indexer.rest.url}")
    String xprtUrl;

    @GetMapping("/validators")
    public Object getValidators(@RequestParam(defaultValue = "10") String num,
                                @RequestParam(defaultValue = "0") String offset) {

        String cacheKey = "validators" + num + offset;

        if(validatorCacheMap.containsKey(cacheKey)) {
            return validatorCacheMap.get(cacheKey);
        }

        UriComponentsBuilder urlBuilder = UriComponentsBuilder.fromHttpUrl(xprtUrl + "/cosmos/staking/v1beta1/validators")
                                                              .queryParam("pagination.count_total", true)
                                                              .queryParam("pagination.reverse", true)
                                                              .queryParam("pagination.limit", num)
                                                              .queryParam("pagination.offset", offset);

        var validators = restTemplate.getForObject(urlBuilder.toUriString(), Map.class);
        validatorCacheMap.put(cacheKey, validators);
        return validators;
    }

    @GetMapping("/validator/{id}")
    public Object getValidatorById(@PathVariable String id) {


        if (validatorCacheMap.containsKey(id)) {
            return validatorCacheMap.get(id);
        }

        UriComponentsBuilder urlBuilder = UriComponentsBuilder.fromHttpUrl(xprtUrl + "/cosmos/staking/v1beta1/validators/").path(id);
        var validatorDetail = restTemplate.getForObject(urlBuilder.toUriString(), Map.class);
        validatorCacheMap.put(id, validatorDetail);
        return validatorDetail;
    }

    @Scheduled(fixedDelay = 900000)
    public void clearValidatorCache() {
        validatorCacheMap.clear();
        System.out.println("Validator cache cleared.");
    }
}
