package com.meoguri.linkocean.domain.profile.command.entity;

import static com.meoguri.linkocean.exception.Preconditions.*;
import static lombok.AccessLevel.*;

import java.io.Serializable;

import javax.persistence.Embeddable;
import javax.persistence.EmbeddedId;
import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Entity
@NoArgsConstructor(access = PROTECTED)
@Table(name = "follow")
@EqualsAndHashCode(of = "id")
public class Follow {

	/* 복합 키 id */
	@EmbeddedId
	private FollowId id;

	public Follow(final Profile follower, final Profile followee) {
		this.id = new FollowId(follower, followee);
	}

	public boolean isFolloweeOf(final Profile profile) {
		return id.getFollowee().equals(profile);
	}

	@Getter
	@Embeddable
	@NoArgsConstructor(access = PROTECTED)
	@EqualsAndHashCode
	static class FollowId implements Serializable {

		@ManyToOne(optional = false)
		@JoinColumn(name = "follower_id")
		private Profile follower;

		@ManyToOne(optional = false)
		@JoinColumn(name = "followee_id")
		private Profile followee;

		public FollowId(final Profile follower, final Profile followee) {
			checkNotNull(follower);
			checkNotNull(followee);
			checkCondition(!follower.equals(followee), "자기 자신을 팔로우 할 수 없습니다");

			this.follower = follower;
			this.followee = followee;
		}
	}
}
