package com.meoguri.linkocean.domain.profile.entity;

import static com.meoguri.linkocean.exception.Preconditions.*;
import static javax.persistence.FetchType.*;
import static lombok.AccessLevel.*;

import javax.persistence.Entity;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.UniqueConstraint;

import com.meoguri.linkocean.domain.BaseIdEntity;

import lombok.Getter;
import lombok.NoArgsConstructor;

/**
 * 팔로우
 * - 중복 팔로우는 불가능하다
 * - 팔로우를 취소할 수 있다
 */
@Getter
@NoArgsConstructor(access = PROTECTED)
@Entity
@Table(
	uniqueConstraints = @UniqueConstraint(columnNames = {"follower_id", "followee_id"})
)
public class Follow extends BaseIdEntity {

	@ManyToOne(fetch = LAZY, optional = false)
	private Profile follower;

	@ManyToOne(fetch = LAZY, optional = false)
	private Profile followee;

	public Follow(final Profile follower, final Profile followee) {
		checkNotNull(follower);
		checkNotNull(followee);
		checkCondition(!follower.equals(followee), "자기 자신을 팔로우 할 수 없습니다");

		this.follower = follower;
		this.followee = followee;
	}
}
