create table if not exists favorite
(
    id          bigint not null auto_increment,
    bookmark_id bigint,
    owner_id    bigint,
    primary key (id)
) engine = InnoDB;
