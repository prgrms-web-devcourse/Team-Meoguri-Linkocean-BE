create table if not exists favorite_category
(
    id         bigint not null auto_increment,
    category   varchar(20),
    profile_id bigint not null,
    primary key (id)
) engine = InnoDB;
