package com.meoguri.linkocean.domain.profile.entity;

import static lombok.AccessLevel.*;

import javax.persistence.EmbeddedId;
import javax.persistence.Entity;
import javax.persistence.Table;

import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Entity
@NoArgsConstructor(access = PROTECTED)
@Table(name = "follow")
public class Follow2 {

	/* 복합 키 id */
	@EmbeddedId
	private FollowId followId;

	public Follow2(final Profile follower, final Profile followee) {
		this.followId = new FollowId(follower, followee);
	}

	public boolean isFolloweeOf(final Profile profile) {
		return followId.getFollowee().equals(profile);
	}
}
