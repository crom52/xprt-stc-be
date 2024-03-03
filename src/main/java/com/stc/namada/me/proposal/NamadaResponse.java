package com.stc.namada.me.proposal;

import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.Value;
import lombok.experimental.FieldDefaults;
import lombok.extern.jackson.Jacksonized;
import org.springframework.lang.Nullable;

import java.util.List;

@Getter
@Builder
@Value
@Jacksonized
@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
@FieldDefaults(level = AccessLevel.PRIVATE)
public class NamadaResponse<T> {
    @Builder.Default
    String message = "OK";
    Integer status = 0;
    @Nullable
    T value;
}

