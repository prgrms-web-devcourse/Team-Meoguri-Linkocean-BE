create table bookmark_tag (
   id bigint not null,
    bookmark_id bigint,
    tag_id bigint,
    primary key (id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;
