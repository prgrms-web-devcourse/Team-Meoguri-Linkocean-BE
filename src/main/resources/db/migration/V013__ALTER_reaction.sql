alter table reaction
    add column profile_id bigint not null;

alter table reaction
    add constraint fk_reaction_profile foreign key (profile_id) references profile (id);