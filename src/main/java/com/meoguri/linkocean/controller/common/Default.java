package com.meoguri.linkocean.controller.common;

public enum Default {
	IMAGE("default-image.png");

	private final String text;

	Default(final String text) {
		this.text = text;
	}

	public String getText() {
		return text;
	}
}
