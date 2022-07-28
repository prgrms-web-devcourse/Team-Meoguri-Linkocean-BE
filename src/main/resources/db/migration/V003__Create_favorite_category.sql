create table favorite_category
(
    id         bigint not null auto_increment,
    category   varchar(20),
    profile_id bigint,
    primary key (id)
) engine=InnoDB;
