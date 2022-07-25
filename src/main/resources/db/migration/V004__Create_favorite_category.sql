create table favorite_category (
       id bigint not null,
        category_id bigint,
        profile_id bigint,
        primary key (id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;
