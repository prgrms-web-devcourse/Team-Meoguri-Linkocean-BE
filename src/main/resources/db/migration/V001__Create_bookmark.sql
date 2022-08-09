create table if not exists bookmark
(
    id               bigint           not null auto_increment,
    category         varchar(20),
    created_at       datetime(6)      not null,
    memo             longtext,
    open_type        tinyint          not null,
    status           varchar(50)      not null,
    title            varchar(50),
    updated_at       datetime(6)      not null,
    link_metadata_id bigint           not null,
    like_count       bigint default 0 not null,
    url              varchar(255)     not null,
    profile_id       bigint           not null,
    primary key (id)
) engine = InnoDB;
