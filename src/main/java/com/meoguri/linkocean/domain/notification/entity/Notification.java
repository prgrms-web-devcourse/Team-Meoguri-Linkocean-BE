package com.meoguri.linkocean.domain.notification.entity;

import static lombok.AccessLevel.*;

import java.util.Map;

import org.bson.types.ObjectId;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.MongoId;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;

/**
 * 알림은
 * 타입을 가지며 대상의 프로필 아이디를 가진다.
 * 타입별 정의된 추가 정보를 info 에 담을 수 있다.
 */
@Getter
@NoArgsConstructor(access = PROTECTED)
@EqualsAndHashCode(of = "id")
@Document
public class Notification {

	@MongoId
	private ObjectId id;

	/* 알람의 종류 */
	private NotificationType type;
	/* 알림 수신자의 프로필 아이디 */
	private long targetProfileId;

	/* 알람에 필요한 추가 정보들 */
	private Map<String, Noti> info;

	/* 알림 생성자 - 타입별 info 의 검증은 하지 않았습니다 */
	public Notification(final NotificationType type, final long targetProfileId, final Map<String, Noti> info) {

		this.type = type;
		this.targetProfileId = targetProfileId;
		this.info = info;
	}
}
