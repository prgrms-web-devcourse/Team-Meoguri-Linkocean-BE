create table users (
   id bigint not null,
    email varchar(255),
    oauth_type varchar(50) not null,
    primary key (id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;
