package com.meoguri.linkocean;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class IndexController {

	@GetMapping("/api/v1/health")
	public String test() {
		return "develop server 진짜 성공이야 ㅁㅊ";
	}
}
