package com.meoguri.linkocean.domain.profile.entity;

import static com.meoguri.linkocean.exception.Preconditions.*;
import static lombok.AccessLevel.*;

import java.io.Serializable;

import javax.persistence.Embeddable;
import javax.persistence.ManyToOne;

import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Embeddable
@NoArgsConstructor(access = PROTECTED)
public class FollowId implements Serializable {

	@ManyToOne(optional = false)
	private Profile follower;

	@ManyToOne(optional = false)
	private Profile followee;

	public FollowId(final Profile follower, final Profile followee) {
		checkNotNull(follower);
		checkNotNull(followee);
		checkCondition(!follower.equals(followee), "자기 자신을 팔로우 할 수 없습니다");

		this.follower = follower;
		this.followee = followee;
	}
}
