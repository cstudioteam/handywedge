
insert into fw_user (id, name, role, country, language, last_login_date)  values('csf-test-app', 'CSFテストユーザ', '管理者', 'jp', 'ja', null);
-- password
insert into fw_user_passwd (id, passwd) values('csf-test-app', '$2a$10$1x/sImsfWiVTJqUbrkbSa.PNTxuwSlTjeAZ.n3/HN651xj20.9DFG');

insert into fw_user (id, name, role, country, language, last_login_date)  values('csf-test-app2', 'CSFテストユーザ2', '一般ユーザー', 'jp', 'ja', null);
-- password2
insert into fw_user_passwd (id, passwd) values('csf-test-app2', '$2a$10$hJotr3E9rBs9zJ77bti4pOQuFDLmxb7oe6.dd/5ITVAU8TrniEjOO');

insert into fw_user (id, name, role, country, language, last_login_date)  values('csf-test-app3', 'CSFテストユーザ3', '一般ユーザー', 'us', 'e', null);
-- password
insert into fw_user_passwd (id, passwd) values('csf-test-app3', '$2a$10$1x/sImsfWiVTJqUbrkbSa.PNTxuwSlTjeAZ.n3/HN651xj20.9DFG');


-- WFテスト用
insert into fw_user (id, name, role, country, language, last_login_date)  values('wf001', 'WFユーザー１', '担当者', 'jp', 'ja', null);
insert into fw_user (id, name, role, country, language, last_login_date)  values('wf002', 'WFユーザー２', '上長', 'jp', 'ja', null);
insert into fw_user (id, name, role, country, language, last_login_date)  values('wf003', 'WFユーザー３', '部門長', 'jp', 'ja', null);
-- パスワード＝「password」
insert into fw_user_passwd (id, passwd) values('wf001', '$2a$10$1x/sImsfWiVTJqUbrkbSa.PNTxuwSlTjeAZ.n3/HN651xj20.9DFG');
insert into fw_user_passwd (id, passwd) values('wf002', '$2a$10$1x/sImsfWiVTJqUbrkbSa.PNTxuwSlTjeAZ.n3/HN651xj20.9DFG');
insert into fw_user_passwd (id, passwd) values('wf003', '$2a$10$1x/sImsfWiVTJqUbrkbSa.PNTxuwSlTjeAZ.n3/HN651xj20.9DFG');

insert into fw_action (action_code, action, pre_status, post_status) values ('00100', N'保存', N'Nothing', N'新規作成');
insert into fw_action (action_code, action, pre_status, post_status) values ('00101', N'申請', N'新規作成', N'上長承認待ち');
insert into fw_action (action_code, action, pre_status, post_status) values ('00201', N'承認（上長）', N'上長承認待ち', N'部門長承認待ち');
insert into fw_action (action_code, action, pre_status, post_status) values ('00202', N'否認（上長）', N'上長承認待ち', N'新規作成');
insert into fw_action (action_code, action, pre_status, post_status) values ('00301', N'承認（部門長）', N'部門長承認待ち', N'決済済み');
insert into fw_action (action_code, action, pre_status, post_status) values ('00302', N'否認（部門長）', N'部門長承認待ち', N'新規作成');

insert into fw_role_action (role, action_code) values(N'担当者','00100');
insert into fw_role_action (role, action_code) values(N'担当者','00101');
insert into fw_role_action (role, action_code) values(N'上長','00100');
insert into fw_role_action (role, action_code) values(N'上長','00101');
insert into fw_role_action (role, action_code) values(N'上長','00201');
insert into fw_role_action (role, action_code) values(N'上長','00202');
insert into fw_role_action (role, action_code) values(N'部門長','00301');
insert into fw_role_action (role, action_code) values(N'部門長','00302');

-- [申請取消し]追加定義
/*
insert into fw_action (action_code, action, pre_status, post_status) values ('00102', N'申請取消し', N'上長承認待ち', N'新規作成');
insert into fw_role_action (role, action_code) values(N'担当者','00102');
*/
