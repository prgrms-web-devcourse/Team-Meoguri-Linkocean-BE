create table if not exists profile
(
    id       bigint      not null auto_increment,
    bio      varchar(50),
    image    varchar(255),
    username varchar(50) not null,
    user_id  bigint,
    primary key (id)
) engine = InnoDB;
