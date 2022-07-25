alter table category add constraint UK_46ccwnsi9409t36lurvtyljak unique (name);
alter table follow add constraint UKrsb4np9gwp6pkj8w9vj06x6hs unique (follower_id, followee_id);
alter table link_metadata add constraint UK_2f87cyb23di9juwtsoqvkypib unique (url);
alter table profile add constraint UK_5em4hwqp4woqsf49dru7fjo80 unique (username);
alter table tag add constraint UK_1wdpsed5kna2y38hnbgrnhi5b unique (name);
alter table users add constraint UK_6dotkott2kjsp8vw4d0m25fb7 unique (email);
alter table bookmark add constraint FKnt6vka9qrm9uxencik3dtn2qj foreign key (link_metadata_id) references link_metadata(id);
alter table bookmark add constraint FKbrwfrudp6lu69r0ah11u0taqn foreign key (profile_id) references profile(id);
alter table bookmark_tag add constraint FKpfa5mq9fkkjmv9jmu4hk9igpw foreign key (bookmark_id) references bookmark(id);
alter table bookmark_tag add constraint FKhq7j2vott6kem0g51hhgq5nfl foreign key (tag_id) references tag(id);
alter table favorite_category add constraint FK5762joeta03e8ggg7wu51bqci foreign key (category_id) references category(id);
alter table favorite_category add constraint FK8i0gnp1e44cb97q8ldpnqaayt foreign key (profile_id) references profile(id);
alter table follow add constraint FK3d25odnplkedd4fh6fbeqiduy foreign key (followee_id) references profile(id);
alter table follow add constraint FK28jq0hbsgm4tqch6co05usp2i foreign key (follower_id) references profile(id);
alter table notification add constraint FK2eau6vfc9hs4fehb83ll1xduf foreign key (bookmark_id) references bookmark(id);
alter table notification add constraint FK2soayv75bmfojpwdy45siai88 foreign key (target_id) references profile(id);
alter table profile add constraint FKs14jvsf9tqrcnly0afsv0ngwv foreign key (user_id) references users(id);
alter table reaction add constraint FKmlvtl9s0vxym1gr8b861706vu foreign key (bookmark_id) references bookmark(id);
