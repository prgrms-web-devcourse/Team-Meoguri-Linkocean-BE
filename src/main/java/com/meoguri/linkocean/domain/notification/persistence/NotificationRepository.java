package com.meoguri.linkocean.domain.notification.persistence;

import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.jpa.repository.JpaRepository;

import com.meoguri.linkocean.domain.notification.entity.Notification;

public interface NotificationRepository extends JpaRepository<Notification, Long> {

	Slice<Notification> findBy(Pageable pageable);
}
