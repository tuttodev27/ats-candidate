package com.ats.candidate.domain.model;

import lombok.*;
import lombok.experimental.FieldDefaults;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Builder
@ToString
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
@FieldDefaults(level = AccessLevel.PRIVATE)
public class LanguageLevel {
    @EqualsAndHashCode.Include
    Long id;
    String code;
    String name;
    Integer orderNumber;
    Boolean active;
}
