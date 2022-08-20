CREATE TABLE IF NOT EXISTS get_bookmark_record
(
    id          BIGINT      NOT NULL AUTO_INCREMENT,
    profile_id  BIGINT      NOT NULL,
    bookmark_id BIGINT      NOT NULL,
    get_at      DATETIME(6) NOT NULL,
    PRIMARY KEY (id)
) ENGINE = InnoDB;

ALTER TABLE get_bookmark_record
    ADD CONSTRAINT uk_profile_id_bookmark_id UNIQUE (profile_id, bookmark_id);

ALTER TABLE get_bookmark_record
    ADD CONSTRAINT fk_get_bookmark_record_profile FOREIGN KEY (profile_id) REFERENCES profile (id);

ALTER TABLE get_bookmark_record
    ADD CONSTRAINT fk_get_bookmark_record_bookmark FOREIGN KEY (bookmark_id) REFERENCES bookmark (id);