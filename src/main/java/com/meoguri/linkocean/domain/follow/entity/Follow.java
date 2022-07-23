package com.meoguri.linkocean.domain.follow.entity;

import static javax.persistence.FetchType.*;
import static lombok.AccessLevel.*;

import javax.persistence.Entity;
import javax.persistence.ManyToOne;

import com.meoguri.linkocean.domain.common.BaseIdEntity;
import com.meoguri.linkocean.domain.profile.entity.Profile;

import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor(access = PROTECTED)
@Entity
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
