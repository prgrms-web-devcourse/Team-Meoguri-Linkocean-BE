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
 * 팔로워 - 팔로이 조합은 유니크하다.
 */
@Getter
@NoArgsConstructor(access = PROTECTED)
@Entity
@Table(
	uniqueConstraints = @UniqueConstraint(columnNames = {"follower_id", "followee_id"})
)
public class Follow extends BaseIdEntity {

	@ManyToOne(fetch = LAZY)
	private Profile follower;

	@ManyToOne(fetch = LAZY)
	private Profile followee;

	public Follow(final Profile follower, final Profile followee) {
		checkNotNull(follower);
		checkNotNull(followee);
		checkCondition(!follower.equals(followee), "자기 자신을 팔로우 할 수 없습니다");

		this.follower = follower;
		this.followee = followee;
	}
}
