create table favorite_category
(
    id          bigint not null auto_increment,
    category_id bigint,
    profile_id  bigint,
    primary key (id)
) engine=InnoDB;
