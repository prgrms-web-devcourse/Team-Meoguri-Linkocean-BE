create table bookmark_tag (
   id bigint not null auto_increment,
    bookmark_id bigint,
    tag_id bigint,
    primary key (id)
) engine=InnoDB;
