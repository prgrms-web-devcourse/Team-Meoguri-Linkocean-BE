alter table notification MODIFY id bigint not null auto_increment;
alter table bookmark_tag MODIFY id bigint not null auto_increment;
alter table favorite_category MODIFY id bigint not null auto_increment;
alter table reaction MODIFY id bigint not null auto_increment;

SET
FOREIGN_KEY_CHECKS = 0;
SET
GLOBAL FOREIGN_KEY_CHECKS=0;

alter table bookmark MODIFY id bigint not null auto_increment;
alter table category MODIFY id bigint not null auto_increment;
alter table link_metadata MODIFY id bigint not null auto_increment;
alter table profile MODIFY id bigint not null auto_increment;
alter table tag MODIFY id bigint not null auto_increment;
alter table users MODIFY id bigint not null auto_increment;

SET
FOREIGN_KEY_CHECKS = 1;
SET
GLOBAL FOREIGN_KEY_CHECKS=1;