drop table fw_role_action;
drop table fw_action;
drop table fw_user_passwd;
drop table fw_api_token;
drop table fw_user;

-- 削除フラグか削除済テーブルを作る？（削除APIを追加するなら）
create table fw_user(
	id varchar(128) not null,
	name varchar(256),
	role varchar(256),
	country varchar(64),
	language varchar(64),
	create_date timestamp not null,
	update_date timestamp not null,
	last_login_date timestamp,
	constraint pk_fw_user primary key(id)
);

create table fw_user_passwd(
	id varchar(128) not null,
	passwd varchar(64) not null,
	create_date timestamp not null,
	update_date timestamp not null,
	constraint pk_fw_user_passwd primary key(id),
	constraint fk_fw_user_passwd foreign key(id) references fw_user(id)
);

create table fw_action(
    id varchar(16) not null,
    pre_status varchar(256) not null,
    post_status varchar(256) not null,
    create_date timestamp not null,
    update_date timestamp not null,
    constraint pk_fw_action primary key(id)
);

create table fw_role_action(
    role varchar(256) not null,
    action_id varchar(16) not null,
    create_date timestamp not null,
    update_date timestamp not null,
    constraint fk_fw_role_action foreign key(action_id) references fw_action(id)
);

create table fw_api_token(
	id varchar(128) not null, 
	token varchar(32) not null,
	create_date timestamp not null,
	constraint pk_fw_api_token primary key(id),
	constraint fk_fw_api_token foreign key(id) references fw_user(id)
);
create unique index idx_fw_api_token on fw_api_token(token);