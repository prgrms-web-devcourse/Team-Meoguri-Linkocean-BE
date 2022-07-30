alter table follow
    add constraint uk_follow_follower_id_followee_id unique (follower_id, followee_id);
alter table link_metadata
    add constraint uk_link_metadata_url unique (url);
alter table profile
    add constraint uk_profile_username unique (username);
alter table reaction
    add constraint uk_reaction_profileId_bookmarkId unique (profile_id, bookmark_id);
alter table tag
    add constraint uk_tag_name unique (name);
alter table users
    add constraint uk_users_email_oauthtype unique (email, oauth_type);
