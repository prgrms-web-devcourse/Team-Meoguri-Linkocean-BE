package com.meoguri.linkocean.domain.notification.entity;

import static javax.persistence.EnumType.*;
import static javax.persistence.FetchType.*;
import static lombok.AccessLevel.*;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Enumerated;
import javax.persistence.ManyToOne;

import org.hibernate.annotations.Type;
import org.hibernate.annotations.TypeDef;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.meoguri.linkocean.domain.BaseIdEntity;
import com.meoguri.linkocean.domain.profile.entity.Profile;
import com.vladmihalcea.hibernate.type.json.JsonType;

import lombok.Getter;
import lombok.NoArgsConstructor;

/**
 * 알림
 * 타입을 가지며 대상의 프로필 아이디를 가진다.
 * 타입별 정의된 추가 정보를 info 에 담을 수 있다.
 */
@Getter
@NoArgsConstructor(access = PROTECTED)
@TypeDef(name = "json", typeClass = JsonType.class)
@Entity
public class Notification extends BaseIdEntity implements Serializable {

	@Column(nullable = false, length = 20)
	@Enumerated(STRING)
	private NotificationType type;

	@JsonIgnore
	@ManyToOne(fetch = LAZY, optional = false)
	private Profile receiver;

	/* 다양한 정보를 담기위해 json 타입을 사용한다 */
	@Type(type = "json")
	@Column(nullable = false, columnDefinition = "jsonb")
	private Map<String, Object> info = new HashMap<>();

	public Notification(final NotificationType type, final Profile receiver, final Map<String, Object> info) {

		this.type = type;
		this.receiver = receiver;
		this.info = info;
	}
}
