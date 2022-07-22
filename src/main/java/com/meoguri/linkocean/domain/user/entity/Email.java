package com.meoguri.linkocean.domain.user.entity;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.Column;
import javax.persistence.Embeddable;

import static lombok.AccessLevel.PROTECTED;

@Getter
@NoArgsConstructor(access = PROTECTED)
@EqualsAndHashCode
@Embeddable
public class Email {

    @Column(name = "email")
    private String email;

    public Email(String email) {
        //TODO validate email - 추가하면 테스트도 추가하세요 안하면 죽음

        this.email = email;
    }
}
