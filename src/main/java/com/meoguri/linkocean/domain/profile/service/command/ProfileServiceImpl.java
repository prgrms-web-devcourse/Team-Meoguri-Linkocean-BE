package com.meoguri.linkocean.domain.profile.service.command;

import static com.meoguri.linkocean.exception.Preconditions.*;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.meoguri.linkocean.domain.profile.entity.FavoriteCategories;
import com.meoguri.linkocean.domain.profile.entity.Profile;
import com.meoguri.linkocean.domain.profile.persistence.command.FindProfileByIdQuery;
import com.meoguri.linkocean.domain.profile.persistence.command.ProfileRepository;
import com.meoguri.linkocean.domain.profile.persistence.query.ProfileQueryRepository;
import com.meoguri.linkocean.domain.profile.service.command.dto.RegisterProfileCommand;
import com.meoguri.linkocean.domain.profile.service.command.dto.UpdateProfileCommand;
import com.meoguri.linkocean.domain.user.service.UserService;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RequiredArgsConstructor
@Transactional(readOnly = true)
@Service
public class ProfileServiceImpl implements ProfileService {

	private final UserService userService;

	private final ProfileRepository profileRepository;
	private final ProfileQueryRepository profileQueryRepository;

	private final FindProfileByIdQuery findProfileByIdQuery;

	@Transactional
	@Override
	public long registerProfile(final RegisterProfileCommand command) {
		final long userId = command.getUserId();
		final String username = command.getUsername();
		final FavoriteCategories favoriteCategories = new FavoriteCategories(command.getCategories());

		/* 비즈니스 로직 검증 - 프로필의 [유저 이름]은 중복 될 수 없다 */
		final boolean exists = profileQueryRepository.existsByUsername(username);
		checkUniqueConstraint(exists, "이미 사용중인 이름입니다.");

		/* 프로필 저장, 유저에 프로필 등록 */
		final Profile profile = profileRepository.save(new Profile(username, favoriteCategories));
		userService.registerProfile(userId, profile);

		log.info("save profile with id :{}, username :{}", profile.getId(), username);
		return profile.getId();
	}

	@Transactional
	@Override
	public void updateProfile(final UpdateProfileCommand command) {
		final long profileId = command.getProfileId();
		final String updateUsername = command.getUsername();

		/* 프로필 조회 */
		final Profile profile = findProfileByIdQuery.findById(profileId);

		/* 비즈니스 로직 검증 - 프로필의 [유저 이름]은 중복 될 수 없다 */
		final boolean exists = profileQueryRepository.existsByUsernameExceptMe(updateUsername, profileId);
		checkUniqueConstraint(exists, "이미 사용중인 이름입니다.");

		/* 프로필 업데이트 */
		profile.update(
			updateUsername, command.getBio(), command.getImage(),
			new FavoriteCategories(command.getCategories())
		);
	}
}
