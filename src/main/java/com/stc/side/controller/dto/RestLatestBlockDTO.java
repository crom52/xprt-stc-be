package com.stc.side.controller.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class RestLatestBlockDTO {
    LatestBlock block;

    @Getter
    @Builder
    @AllArgsConstructor
    @NoArgsConstructor
    public static class LatestBlock {
        BlockHeader header;
    }

    @Getter
    @Builder
    @AllArgsConstructor
    @NoArgsConstructor
    public static class BlockHeader {
        String height;
    }
}
