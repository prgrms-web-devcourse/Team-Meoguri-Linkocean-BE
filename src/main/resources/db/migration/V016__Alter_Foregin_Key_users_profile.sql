alter table profile
    drop foreign key fk_profile_users;

alter table users
    add profile_id bigint null;

alter table users
    add constraint fk_users_profile
        foreign key (profile_id) references profile (id);

update users u
set profile_id = (select id
                  from profile p
                  where p.user_id = u.id)
where 1 = 1;

alter table profile
    drop column user_id;
