create table profile (
   id bigint not null,
    bio varchar(50),
    image_url varchar(255),
    username varchar(50) not null,
    user_id bigint,
    primary key (id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;
