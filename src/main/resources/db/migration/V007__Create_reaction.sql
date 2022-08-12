create table if not exists reaction
(
    id
    bigint
    not
    null
    auto_increment,
    type
    varchar
(
    255
),
    bookmark_id bigint not null,
    profile_id bigint not null,
    primary key
(
    id
)
    ) engine = InnoDB;
