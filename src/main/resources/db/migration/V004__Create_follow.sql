create table if not exists follow
(
    id          bigint not null auto_increment,
    followee_id bigint not null,
    follower_id bigint not null,
    primary key (id)
) engine = InnoDB;
