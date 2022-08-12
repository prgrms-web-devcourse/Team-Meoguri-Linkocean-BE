package com.meoguri.linkocean.domain;

import static javax.persistence.GenerationType.*;
import static lombok.AccessLevel.*;

import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.MappedSuperclass;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor(access = PROTECTED)
@EqualsAndHashCode
@MappedSuperclass
public class BaseIdEntity {

	@Id
	@GeneratedValue(strategy = IDENTITY)
	private Long id;
}
