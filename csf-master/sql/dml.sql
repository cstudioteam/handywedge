insert into fw_user (id, name, role, country, language, last_login_date)  values('csf-test-app', 'CSFテストユーザ', '管理者', 'jp', 'ja', null);
-- password
insert into fw_user_passwd (id, passwd) values('csf-test-app', '$2a$10$1x/sImsfWiVTJqUbrkbSa.PNTxuwSlTjeAZ.n3/HN651xj20.9DFG');

insert into fw_user (id, name, role, country, language, last_login_date)  values('csf-test-app2', 'CSFテストユーザ2', '一般ユーザー(英語)', 'us', 'en', null);
-- password2
insert into fw_user_passwd (id, passwd) values('csf-test-app2', '$2a$10$hJotr3E9rBs9zJ77bti4pOQuFDLmxb7oe6.dd/5ITVAU8TrniEjOO');
