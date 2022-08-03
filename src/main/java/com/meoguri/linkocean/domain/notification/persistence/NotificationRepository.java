package com.meoguri.linkocean.domain.notification.persistence;

import java.util.List;

import org.bson.types.ObjectId;
import org.springframework.data.mongodb.repository.MongoRepository;

import com.meoguri.linkocean.domain.notification.entity.Notification;

public interface NotificationRepository extends MongoRepository<Notification, ObjectId> {

	List<Notification> findByTargetProfileId(long targetProfileId);
}
