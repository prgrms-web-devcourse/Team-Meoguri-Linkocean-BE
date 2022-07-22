package com.meoguri.linkocean.domain.user.entity;

import com.meoguri.linkocean.domain.common.BaseIdEntity;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.Embedded;
import javax.persistence.Entity;
import javax.persistence.Enumerated;
import javax.persistence.Table;

import static javax.persistence.EnumType.STRING;
import static lombok.AccessLevel.PROTECTED;

@Getter
@NoArgsConstructor(access = PROTECTED)
@Entity
@Table(name = "users")
public class User extends BaseIdEntity {

    @Enumerated(STRING)
    private OAuthType oAuthType;

    @Embedded
    private Email email;

    /**
     * 회원 가입시 사용하는 생성자
     */
    public User(OAuthType oAuthType, Email email) {

        this.oAuthType = oAuthType;
        this.email = email;
    }
}
