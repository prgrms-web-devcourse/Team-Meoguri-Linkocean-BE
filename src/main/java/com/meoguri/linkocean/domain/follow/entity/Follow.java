package com.meoguri.linkocean.domain.follow.entity;

import static javax.persistence.FetchType.*;
import static lombok.AccessLevel.*;

import javax.persistence.Entity;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.UniqueConstraint;

import com.meoguri.linkocean.domain.common.BaseIdEntity;
import com.meoguri.linkocean.domain.profile.entity.Profile;

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

		this.follower = follower;
		this.followee = followee;
	}
}
