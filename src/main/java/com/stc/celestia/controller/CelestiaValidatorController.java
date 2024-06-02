package com.stc.celestia.controller;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.util.CollectionUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@RequestMapping("/stc/celestia")
@RestController
@FieldDefaults(level = AccessLevel.PRIVATE)
@RequiredArgsConstructor
public class CelestiaValidatorController {
    final RestTemplate restTemplate;

    static Map<String, Object> validatorCacheMap = new ConcurrentHashMap<>();

    @Value("${indexer.rest.celestia}")
    String celestiaUrl;

    @GetMapping("/validators")
    public Object getValidators() {
        UriComponentsBuilder urlBuilder = UriComponentsBuilder.fromHttpUrl(celestiaUrl + "/api/v1/validators");
        var validatorsResponse = restTemplate.getForObject(urlBuilder.toUriString(), List.class);
        if(CollectionUtils.isEmpty(validatorsResponse)) {
            return List.of();
        }
        validatorsResponse.forEach(e -> validatorCacheMap.put(((Map)e).get("operatorAddress").toString(), e ));
        return validatorsResponse;
    }

    @GetMapping("/validator/{idOrMoniker}")
    public Object getValidatorById(@PathVariable String idOrMoniker) {
        if(!idOrMoniker.startsWith("celestiavaloper")) {
            return "Not ID format";
        }

        if (validatorCacheMap.containsKey(idOrMoniker)) {
            return validatorCacheMap.get(idOrMoniker);
        }
        getValidators();
        return validatorCacheMap.get(idOrMoniker);
    }

    @Scheduled(fixedDelay = 300000)
    public void clearValidatorCache() {
        validatorCacheMap.clear();
        System.out.println("Validator cache cleared.");
    }
}
