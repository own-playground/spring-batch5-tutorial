package com.tally.batch.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Getter
@Setter
@NoArgsConstructor
public class AfterEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String username;

    @Builder
    public AfterEntity(final String username) {
        this.username = username;
    }

    public static AfterEntity created(final BeforeEntity item) {
        return AfterEntity.builder()
                .username(item.getUsername())
                .build();
    }
}