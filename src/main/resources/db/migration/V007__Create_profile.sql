create table profile
(
    id        bigint      not null auto_increment,
    bio       varchar(50),
    image_url varchar(255),
    username  varchar(50) not null,
    user_id   bigint,
    primary key (id)
) engine=InnoDB;