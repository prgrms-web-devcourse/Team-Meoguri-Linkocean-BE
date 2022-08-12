create table if not exists favorite
(
    id
    bigint
    not
    null
    auto_increment,
    bookmark_id
    bigint
    not
    null,
    owner_id
    bigint
    not
    null,
    primary
    key
(
    id
)
    ) engine = InnoDB;
