alter table users
    drop index uk_users_email;

alter table users
    add constraint uk_users_email_oauthtype unique (email, oauth_type);
