package com.stc.namada.me.validator;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.AccessLevel;
import lombok.experimental.FieldDefaults;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.Map;
import java.util.Optional;

import static com.fasterxml.jackson.databind.DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES;
import static com.fasterxml.jackson.databind.PropertyNamingStrategy.SNAKE_CASE;

@RequestMapping("/stc/namada-me")
@RestController
@FieldDefaults(level = AccessLevel.PRIVATE)
public class ValidatorController {
    final RestTemplate restTemplate = new RestTemplate();
    final ObjectMapper objectMapper = new ObjectMapper().setPropertyNamingStrategy(SNAKE_CASE)
                                                        .disable(FAIL_ON_UNKNOWN_PROPERTIES);

    @Value("${indexer.api.url}")
    String indexerUrl;

    @GetMapping("/validators")
    public Object getValidators(@RequestParam(defaultValue = "10") String num,
                                @RequestParam(defaultValue = "1") String offset) throws JsonProcessingException {


        LatestBlock latestBlock = restTemplate.getForObject(indexerUrl + "/block/last", LatestBlock.class);

        if (latestBlock == null || latestBlock.getHeader() == null) {
            return ValidatorsResponse.builder().build();
        }

        String latestBlockHeight = latestBlock.getHeader().getHeight();
        UriComponentsBuilder urlBuilder = UriComponentsBuilder.fromHttpUrl(indexerUrl + "/validator/list")
                                                              .queryParam("num", num).queryParam("offset", offset);

        Optional<String> finalValidatorsResult;
        String latestValidatorUrl = urlBuilder.queryParam("height", latestBlockHeight).toUriString();
        finalValidatorsResult = Optional.ofNullable(restTemplate.getForObject(latestValidatorUrl, Map.class))
                                        .map(e -> e.get("data")).map(Object::toString);

        if (finalValidatorsResult.isEmpty()) {
            String stableHeight = "35202";
            String stableValidatorUrl = urlBuilder.queryParam("height", stableHeight).toUriString();
            finalValidatorsResult = Optional.ofNullable(restTemplate.getForObject(stableValidatorUrl, Map.class))
                                            .map(e -> String.valueOf(e.get("data")));
        }

        if (finalValidatorsResult.isEmpty()) {
            return ValidatorsResponse.builder().build();
        }

        ValidatorInnerData validatorsValidatorInnerData = objectMapper.readValue(finalValidatorsResult.get(),
                                                                                 ValidatorInnerData.class);
        return ValidatorsResponse.builder().data(validatorsValidatorInnerData).build();
    }

//    public static void main(String[] args) {
//        try {
//            // Execute the command
//            Process process = Runtime.getRuntime().exec("echo 'hello'");
//
//            // Read the output of the command
//            BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()));
//            StringBuilder output = new StringBuilder();
//            String line;
//            while ((line = reader.readLine()) != null) {
//                output.append(line).append("\n");
//            }
//
//            // Wait for the command to finish
//            process.waitFor();
//
//            // Close the reader
//            reader.close();
//
//            System.out.println(output.toString());
//        } catch (Exception e) {
//            e.printStackTrace();
//            System.out.println("Error executing command: " + e.getMessage());
//        }
//    }
}
