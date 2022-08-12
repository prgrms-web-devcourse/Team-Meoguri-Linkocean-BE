create table if not exists bookmark_tag
(
    id          bigint not null auto_increment,
    bookmark_id bigint not null,
    tag_id      bigint not null,
    primary key (id)
) engine = InnoDB;
