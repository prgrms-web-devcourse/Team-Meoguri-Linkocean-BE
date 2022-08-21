package com.meoguri.linkocean.domain.profile.entity;

import static lombok.AccessLevel.*;

import java.util.HashSet;
import java.util.Set;

import javax.persistence.CollectionTable;
import javax.persistence.ElementCollection;
import javax.persistence.Embeddable;
import javax.persistence.JoinColumn;

import lombok.NoArgsConstructor;

@Embeddable
@NoArgsConstructor(access = PROTECTED)
public class Reactions {

	@ElementCollection
	@CollectionTable(
		name = "reaction",
		joinColumns = @JoinColumn(name = "profile_id")
	)
	private Set<Reaction> reactions = new HashSet<>();
}
