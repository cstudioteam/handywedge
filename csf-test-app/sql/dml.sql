
insert into fw_user (id, name, role, country, language, last_login_date)  values('csf-test-app', 'CSFテストユーザ', '管理者', 'jp', 'ja', null);
-- password
insert into fw_user_passwd (id, passwd) values('csf-test-app', '$2a$10$1x/sImsfWiVTJqUbrkbSa.PNTxuwSlTjeAZ.n3/HN651xj20.9DFG');

insert into fw_user (id, name, role, country, language, last_login_date)  values('csf-test-app2', 'CSFテストユーザ2', '一般ユーザー', 'jp', 'ja', null);
-- password2
insert into fw_user_passwd (id, passwd) values('csf-test-app2', '$2a$10$hJotr3E9rBs9zJ77bti4pOQuFDLmxb7oe6.dd/5ITVAU8TrniEjOO');

insert into fw_user (id, name, role, country, language, last_login_date)  values('csf-test-app3', 'CSFテストユーザ3', '一般ユーザー', 'us', 'e', null);
-- password
insert into fw_user_passwd (id, passwd) values('csf-test-app3', '$2a$10$1x/sImsfWiVTJqUbrkbSa.PNTxuwSlTjeAZ.n3/HN651xj20.9DFG');

/* イメージ
insert into fw_action (action_code, action, pre_status, post_status) values ('00100', 'WF申請', '新規申請', 'WF申請承認待ち');
insert into fw_action (action_code, action, pre_status, post_status) values ('00101', 'WF申請承認', 'WF申請承認待ち', 'WF申請承認済み');
insert into fw_action (action_code, action, pre_status, post_status) values ('00102', 'WF申請否認', 'WF申請承認待ち', 'WF申請否認済み');
insert into fw_action (action_code, action, pre_status, post_status) values ('00103', 'WF申請再申請', 'WF申請否認', 'WF申請承認待ち');
*/
