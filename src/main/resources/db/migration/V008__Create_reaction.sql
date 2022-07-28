create table reaction
(
    id          bigint not null auto_increment,
    type        varchar(255),
    bookmark_id bigint,
    profile_id  bigint,
    primary key (id)
) engine=InnoDB;
