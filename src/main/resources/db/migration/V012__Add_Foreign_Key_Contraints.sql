alter table bookmark
    add constraint fk_bookmark_link_metadata foreign key (link_metadata_id) references link_metadata (id);
alter table bookmark
    add constraint fk_bookmark_profile foreign key (profile_id) references profile (id);
alter table bookmark_tag
    add constraint fk_bookmarkTag_bookmark foreign key (bookmark_id) references bookmark (id);
alter table bookmark_tag
    add constraint fk_bookmarkTag_tag foreign key (tag_id) references tag (id);
alter table favorite_category
    add constraint fk_favoriteCategory_profile foreign key (profile_id) references profile (id);
alter table follow
    add constraint fk_follow_followee foreign key (followee_id) references profile (id);
alter table follow
    add constraint fk_follow_follower foreign key (follower_id) references profile (id);
alter table notification
    add constraint fk_notification_bookmark foreign key (bookmark_id) references bookmark (id);
alter table notification
    add constraint fk_notification_target foreign key (target_id) references profile (id);
alter table profile
    add constraint fk_profile_users foreign key (user_id) references users (id);
alter table reaction
    add constraint fk_reaction_bookmark foreign key (bookmark_id) references bookmark (id);
alter table reaction
    add constraint fk_reaction_profile foreign key (profile_id) references profile (id);
