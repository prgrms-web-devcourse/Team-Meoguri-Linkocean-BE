create table if not exists link_metadata
(
    id    bigint       not null auto_increment,
    image varchar(255) not null,
    title varchar(255) not null,
    link  varchar(255) not null,
    primary key (id)
) engine = InnoDB;
