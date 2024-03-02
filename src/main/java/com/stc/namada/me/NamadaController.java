package com.stc.namada.me;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import lombok.AccessLevel;
import lombok.experimental.FieldDefaults;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.PostConstruct;
import java.util.Map;

@RestController
@RequestMapping("/stc//namada-me")
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class NamadaController {
    ObjectMapper objectMapper = new ObjectMapper().setPropertyNamingStrategy(PropertyNamingStrategies.SNAKE_CASE);

    @GetMapping("/proposals")
    public ProposalResponse getProposals() throws JsonProcessingException {
        return objectMapper.readValue(ProposalMockData.proposal, ProposalResponse.class);
    }
}
