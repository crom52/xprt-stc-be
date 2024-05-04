package com.stc.xprt.me.proposal;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

@RestController
@RequestMapping("/stc/xprt")
@FieldDefaults(level = AccessLevel.PRIVATE)
@RequiredArgsConstructor
public class ProposalController {
    @Value("${indexer.rest.url}")
    String xprtUrl;

    final RestTemplate restTemplate;
    static Map<String, Object> proposalsCacheMap = new ConcurrentHashMap<>();



    @GetMapping("/proposals")
    public Object getProposals(
            @RequestParam(value = "proposal_status", required = false) String proposalStatus,
            @RequestParam(required = false, value = "voter") String voter,
            @RequestParam(required = false, value = "depositor") String depositor,
            @RequestParam(required = false, value = "key") String key,
            @RequestParam(required = false, value = "num", defaultValue = "10") Integer num,
            @RequestParam(required = false, value = "offset", defaultValue = "0") Integer offset) {
        String cacheKey = proposalStatus+num.toString()+offset.toString();
        if(proposalsCacheMap.containsKey(cacheKey)) {
            return proposalsCacheMap.get(cacheKey);
        }
        Optional<String> optStatus = proposalStatus.isBlank() ? Optional.empty() : Optional.of(proposalStatus);
        UriComponentsBuilder urlBuilder = UriComponentsBuilder.fromHttpUrl(xprtUrl).path("cosmos/gov/v1/proposals")
                                                              .queryParamIfPresent("proposal_status", optStatus)
                                                              .queryParam("voter", voter)
                                                              .queryParam("depositor", depositor).queryParam("key", key)
                                                              .queryParam("pagination.offset", offset)
                                                              .queryParam("pagination.limit", num)
                                                              .queryParam("pagination.count_total", true)
                                                              .queryParam("pagination.reverse", true);

        var proposals= restTemplate.getForObject(urlBuilder.toUriString(), Map.class);
        proposalsCacheMap.put(cacheKey, proposals);
        return proposals;
    }

    @Scheduled(fixedDelay = 900000)
    public void clearLastBlockCache() {
        proposalsCacheMap.clear();
        System.out.println("Proposals cache cleared.");
    }
}
