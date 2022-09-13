package com.meoguri.linkocean.domain.profile.entity;

import static com.meoguri.linkocean.exception.Preconditions.*;
import static lombok.AccessLevel.*;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Embeddable;
import javax.persistence.EmbeddedId;
import javax.persistence.Entity;
import javax.persistence.JoinColumn;
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

	public Follow(final long followerId, final long followeeId) {
		this.id = new FollowId(followerId, followeeId);
	}

	public boolean isFolloweeOf(final Profile profile) {
		return id.getFolloweeId() == profile.getId();
	}

	@Getter
	@Embeddable
	@NoArgsConstructor(access = PROTECTED)
	@EqualsAndHashCode
	static class FollowId implements Serializable {

		@Column(nullable = false)
		@JoinColumn(name = "follower_id")
		private long followerId;

		@Column(nullable = false)
		@JoinColumn(name = "followee_id")
		private long followeeId;

		public FollowId(final long followerId, final long followeeId) {
			checkCondition(followerId != followeeId, "자기 자신을 팔로우 할 수 없습니다");

			this.followerId = followerId;
			this.followeeId = followeeId;
		}
	}
}
