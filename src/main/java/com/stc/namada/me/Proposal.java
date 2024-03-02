package com.stc.namada.me;

import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.experimental.FieldDefaults;

@Getter
@Builder
@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
@FieldDefaults(level = AccessLevel.PRIVATE)
@AllArgsConstructor
@NoArgsConstructor
public class Proposal {
    Integer id;
    Content content;
    String type;
    String author;
    Integer votingStartEpoch;
    Integer votingEndEpoch;
    Integer graceEpoch;
    String votes;

    @Getter
    @Builder
    @JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
    @FieldDefaults(level = AccessLevel.PRIVATE)
    public static class Content {
        String abstractText;
        String authors;
        String created;
        String details;
        String discussionsTo;
        String license;
        String motivation;
        String title;
    }
}
