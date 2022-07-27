create table link_metadata (
   id bigint not null auto_increment,
    image_url varchar(255) not null,
    title varchar(255) not null,
    url varchar(255) not null,
    primary key (id)
) engine=InnoDB;
