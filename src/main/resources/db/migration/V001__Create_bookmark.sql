create table bookmark (
   id bigint not null,
    created_at timestamp not null,
    memo LONGTEXT,
    open_type varchar(50) not null,
    title varchar(50),
    updated_at timestamp not null,
    link_metadata_id bigint,
    profile_id bigint,
    primary key (id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;
