package com.meoguri.linkocean.controller.user.dto;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

/**
 * 로그인이 성공하면 사용자가 정상적인
 * 회원가입 절차를 통해 프로필이 등록 된 사용자인지 알려준다
 */
@Getter
@RequiredArgsConstructor
public class LoginSuccessResponse {

	private final boolean hasProfile;

}
