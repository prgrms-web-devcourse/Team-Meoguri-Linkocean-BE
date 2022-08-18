alter table favorite_category
    drop foreign key fk_favoriteCategory_profile;

alter table favorite_category
    drop column id;
