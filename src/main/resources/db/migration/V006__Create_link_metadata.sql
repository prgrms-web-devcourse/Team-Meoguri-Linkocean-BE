create table link_metadata (
   id bigint not null,
    image_url varchar(255) not null,
    title varchar(255) not null,
    url varchar(255) not null,
    primary key (id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;
