CREATE TABLE IF NOT EXISTS notification
(
    id          BIGINT      NOT NULL AUTO_INCREMENT,
    info        JSON        NOT NULL,
    type        VARCHAR(20) NOT NULL,
    receiver_id BIGINT      NOT NULL,
    PRIMARY KEY (id)
) ENGINE = InnoDB;

ALTER TABLE notification
    ADD CONSTRAINT fk_notification_receiver FOREIGN KEY (receiver_id) REFERENCES profile (id);