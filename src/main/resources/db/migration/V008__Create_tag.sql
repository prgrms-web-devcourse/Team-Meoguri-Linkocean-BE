create table if not exists tag
(
    id   bigint      not null auto_increment,
    name varchar(50) not null,
    primary key (id)
) engine = InnoDB;
