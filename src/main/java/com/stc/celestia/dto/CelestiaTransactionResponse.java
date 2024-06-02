package com.stc.celestia.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;

import java.util.List;

@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class CelestiaTransactionResponse {
    List<Data> data;

    @Getter
    @Builder
    @AllArgsConstructor
    @NoArgsConstructor
    public static class Data {
        Long height;
        String hash;
        String timestamp;
        Integer code;
        Fee fee;
        List<String> messages;

        @Getter
        @Builder
        @AllArgsConstructor
        @NoArgsConstructor
        public static class Fee {
            Long amount;
            String denom;
        }
    }
}
