ALTER TABLE bookmark
    DROP FOREIGN KEY fk_bookmark_profile;

ALTER TABLE bookmark
    DROP INDEX uk_bookmark_profileId_url;

ALTER TABLE bookmark
    ADD CONSTRAINT fk_bookmark_profile FOREIGN KEY (profile_id) REFERENCES profile (id);
