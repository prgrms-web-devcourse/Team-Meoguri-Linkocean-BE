package com.meoguri.linkocean.domain.profile.command.persistence;

public interface CustomProfileRepository {
	/* 사용자 이름 중복 확인 - 프로필 등록시 사용 */
	boolean existsByUsername(String username);

	/* profileId 의 것이 아닌 사용자 이름 중 updateUsername 의 중복 확인 - 프로필 수정시 사용 */
	boolean existsByUsernameExceptMe(String updateUsername, long profileId);
}
