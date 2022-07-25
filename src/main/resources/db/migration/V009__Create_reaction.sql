create table reaction (
   id bigint not null,
    type varchar(255),
    bookmark_id bigint,
    primary key (id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;
