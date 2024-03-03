package com.stc.namada.me.validator;

import lombok.AccessLevel;
import lombok.experimental.FieldDefaults;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import javax.annotation.PostConstruct;
import java.util.Map;

@RequestMapping("/stc/namada-me")
@RestController
@FieldDefaults(level = AccessLevel.PRIVATE)
public class ValidatorController {
    final RestTemplate restTemplate = new RestTemplate();
    @Value("${indexer.api.url}")
    String indexerUrl;

    @GetMapping("/validators")
    public Object getValidators(@RequestParam String height, @RequestParam(defaultValue = "10") String num,
                                            @RequestParam(defaultValue = "1") String offset) {
        String url = UriComponentsBuilder.fromHttpUrl(indexerUrl + "/validator/list")
                                         .queryParam("height", height)
                                         .queryParam("num", num)
                                         .queryParam("offset", offset)
                                         .toUriString();

        return restTemplate.getForObject(url, Map.class);
    }
}
