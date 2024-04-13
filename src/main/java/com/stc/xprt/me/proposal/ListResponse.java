package com.stc.xprt.me.proposal;

import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.FieldDefaults;

import java.util.List;

@Getter
@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
@FieldDefaults(level = AccessLevel.PRIVATE)
public class ListResponse<T> {
    @Setter
    List<T> list;

    public static <T> ListResponse<T> of(List<T> list) {
        ListResponse<T> response = new ListResponse<>();
        response.setList(list);
        return response;
    }
}
