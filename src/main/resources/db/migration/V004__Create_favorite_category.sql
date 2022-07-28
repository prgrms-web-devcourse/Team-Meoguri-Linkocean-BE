create table favorite_category
(
    id         bigint not null auto_increment,
    category   varchar(255),
    profile_id bigint,
    primary key (id)
) engine=InnoDB;
