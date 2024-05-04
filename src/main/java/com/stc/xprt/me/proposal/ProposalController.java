package com.stc.xprt.me.proposal;

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

@RestController
@RequestMapping("/stc/xprt")
@FieldDefaults(level = AccessLevel.PRIVATE)
@RequiredArgsConstructor
public class ProposalController {
    @Value("${indexer.rest.url}")
    String xprtUrl;

    final RestTemplate restTemplate;


    @GetMapping("/proposals")
    public Object getProposals(
            @RequestParam(value = "proposal_status", defaultValue = "PROPOSAL_STATUS_UNSPECIFIED") String proposalStatus,
            @RequestParam(required = false, value = "voter") String voter,
            @RequestParam(required = false, value = "depositor") String depositor,
            @RequestParam(required = false, value = "key") String key,
            @RequestParam(required = false, value = "num", defaultValue = "10") Integer num,
            @RequestParam(required = false, value = "offset", defaultValue = "0") Integer offset) {
        UriComponentsBuilder urlBuilder = UriComponentsBuilder.fromHttpUrl(xprtUrl).path("cosmos/gov/v1/proposals")
                                                              .queryParam("voter", voter)
                                                              .queryParam("depositor", depositor).queryParam("key", key)
                                                              .queryParam("proposal_status", proposalStatus)
                                                              .queryParam("pagination.offset", offset)
                                                              .queryParam("pagination.limit", num)
                                                              .queryParam("pagination.count_total", true)
                                                              .queryParam("pagination.reverse", true);

        return restTemplate.getForObject(urlBuilder.toUriString(), Map.class);
    }
}
