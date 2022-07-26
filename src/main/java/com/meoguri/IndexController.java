package com.meoguri;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class IndexController {

	@GetMapping
	public String test(){
		return "deploy success";
	}
}
