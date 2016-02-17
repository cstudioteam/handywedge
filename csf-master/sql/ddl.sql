drop table fw_user_passwd;
drop table fw_user;

create table fw_user(
	id varchar(128) not null,
	name varchar(256) not null,
	country varchar(64),
	language varchar(64),
	create_date timestamp not null,
	constraint pk_fw_user primary key(id)
);

create table fw_user_passwd(
	id varchar(128) not null,
	passwd varchar(64) not null,
	create_date timestamp not null,
	constraint pk_fw_user_passwd primary key(id),
	constraint fk_fw_user_passwd foreign key(id) references fw_user(id)
);