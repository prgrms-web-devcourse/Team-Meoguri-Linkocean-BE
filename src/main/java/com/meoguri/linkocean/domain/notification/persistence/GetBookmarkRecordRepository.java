package com.meoguri.linkocean.domain.notification.persistence;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.meoguri.linkocean.domain.notification.entity.GetBookmarkRecord;

public interface GetBookmarkRecordRepository extends JpaRepository<GetBookmarkRecord, Long> {

	Optional<GetBookmarkRecord> findByProfileIdAndBookmarkId(long profileId, long bookmarkId);
}
