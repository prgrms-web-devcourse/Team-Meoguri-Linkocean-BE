create table if not exists users
(
    id         bigint      not null auto_increment,
    email      varchar(255),
    oauth_type varchar(50) not null,
    primary key (id)
) engine = InnoDB;
