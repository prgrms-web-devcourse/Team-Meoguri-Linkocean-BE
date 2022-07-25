alter table category add constraint uk_category_name unique (name);
alter table follow add constraint uk_follow_follower_followee unique (follower_id, followee_id);
alter table link_metadata add constraint uk_link_metadata_url unique (url);
alter table profile add constraint uk_profile_username unique (username);
alter table tag add constraint uk_tag_name unique (name);
alter table users add constraint uk_users_email unique (email);
alter table bookmark add constraint fk_bookmark_link_metadata foreign key (link_metadata_id) references link_metadata(id);
alter table bookmark add constraint fk_bookmark_profile foreign key (profile_id) references profile(id);
alter table bookmark_tag add constraint fk_bookmark_tag_bookmark foreign key (bookmark_id) references bookmark(id);
alter table bookmark_tag add constraint fk_bookmark_tag_tag foreign key (tag_id) references tag(id);
alter table favorite_category add constraint fk_favorite_category_category foreign key (category_id) references category(id);
alter table favorite_category add constraint fk_favorite_category_profile foreign key (profile_id) references profile(id);
alter table follow add constraint fk_follow_profile_followee foreign key (followee_id) references profile(id);
alter table follow add constraint fk_follow_profile_follower foreign key (follower_id) references profile(id);
alter table notification add constraint fk_notification_bookmark foreign key (bookmark_id) references bookmark(id);
alter table notification add constraint fk_notification_profile foreign key (target_id) references profile(id);
alter table profile add constraint fk_notification_users foreign key (user_id) references users(id);
alter table reaction add constraint fk_reaction_bookmark foreign key (bookmark_id) references bookmark(id);
