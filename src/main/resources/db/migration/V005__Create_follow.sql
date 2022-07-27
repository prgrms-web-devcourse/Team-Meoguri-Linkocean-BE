create table follow (
   id bigint not null auto_increment,
    followee_id bigint,
    follower_id bigint,
    primary key (id)
) engine=InnoDB;
