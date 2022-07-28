create table notification
(
    id          bigint not null auto_increment,
    created_at  datetime(6),
    bookmark_id bigint,
    target_id   bigint,
    primary key (id)
) engine=InnoDB;
