
insert into fw_user (id, name, role, country, language, last_login_date)  values('handywedge-test-app', 'handywedgeテストユーザ', '管理者', 'jp', 'ja', null);
-- password
insert into fw_user_passwd (id, passwd) values('handywedge-test-app', '$2a$10$1x/sImsfWiVTJqUbrkbSa.PNTxuwSlTjeAZ.n3/HN651xj20.9DFG');

insert into fw_user (id, name, role, country, language, last_login_date)  values('handywedge-test-app2', 'handywedgeテストユーザ2', '一般ユーザー', 'jp', 'ja', null);
-- password2
insert into fw_user_passwd (id, passwd) values('handywedge-test-app2', '$2a$10$hJotr3E9rBs9zJ77bti4pOQuFDLmxb7oe6.dd/5ITVAU8TrniEjOO');

insert into fw_user (id, name, role, country, language, last_login_date)  values('handywedge-test-app3', 'handywedgeテストユーザ3', '一般ユーザー', 'us', 'e', null);
-- password
insert into fw_user_passwd (id, passwd) values('handywedge-test-app3', '$2a$10$1x/sImsfWiVTJqUbrkbSa.PNTxuwSlTjeAZ.n3/HN651xj20.9DFG');


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

--insert into fw_action (action_code, action, pre_status, post_status) values ('00102', N'申請取消し', N'上長承認待ち', N'新規作成');
--insert into fw_role_action (role, action_code) values(N'担当者','00102');


-- 新WFテスト用
insert into fw_user (id, name, role, country, language, last_login_date)  values('wf2001', N'WFユーザーA部担当', 'RLA0001', 'jp', 'ja', null);
insert into fw_user (id, name, role, country, language, last_login_date)  values('wf2002', N'WFユーザーA部部門長', 'RLA0002', 'jp', 'ja', null);
insert into fw_user (id, name, role, country, language, last_login_date)  values('wf2003', N'WFユーザーB部担当', 'RLB0001', 'jp', 'ja', null);
insert into fw_user (id, name, role, country, language, last_login_date)  values('wf2004', N'WFユーザー事業部長', 'RLX0001', 'jp', 'ja', null);
insert into fw_user (id, name, role, country, language, last_login_date)  values('wf2005', N'WFユーザー役員', 'RLY0001', 'jp', 'ja', null);
insert into fw_user (id, name, role, country, language, last_login_date)  values('wf2006', N'WFユーザー社長', 'RLZ0001', 'jp', 'ja', null);
-- パスワード＝「password」
insert into fw_user_passwd (id, passwd) values('wf2001', '$2a$10$1x/sImsfWiVTJqUbrkbSa.PNTxuwSlTjeAZ.n3/HN651xj20.9DFG');
insert into fw_user_passwd (id, passwd) values('wf2002', '$2a$10$1x/sImsfWiVTJqUbrkbSa.PNTxuwSlTjeAZ.n3/HN651xj20.9DFG');
insert into fw_user_passwd (id, passwd) values('wf2003', '$2a$10$1x/sImsfWiVTJqUbrkbSa.PNTxuwSlTjeAZ.n3/HN651xj20.9DFG');
insert into fw_user_passwd (id, passwd) values('wf2004', '$2a$10$1x/sImsfWiVTJqUbrkbSa.PNTxuwSlTjeAZ.n3/HN651xj20.9DFG');
insert into fw_user_passwd (id, passwd) values('wf2005', '$2a$10$1x/sImsfWiVTJqUbrkbSa.PNTxuwSlTjeAZ.n3/HN651xj20.9DFG');
insert into fw_user_passwd (id, passwd) values('wf2006', '$2a$10$1x/sImsfWiVTJqUbrkbSa.PNTxuwSlTjeAZ.n3/HN651xj20.9DFG');

insert into fw_role_master (role, role_name) values('RLA0001', N'担当者(A部)');
insert into fw_role_master (role, role_name) values('RLA0002', N'部門長(A部)');
insert into fw_role_master (role, role_name) values('RLB0001', N'担当者(B部)');
insert into fw_role_master (role, role_name) values('RLX0001', N'事業部長');
insert into fw_role_master (role, role_name) values('RLY0001', N'役員');
insert into fw_role_master (role, role_name) values('RLZ0001', N'社長');

insert into fw_status_master (status, status_name) values('ST10000', N'Nothing');
insert into fw_status_master (status, status_name) values('ST10001', N'新規作成[A部]');
insert into fw_status_master (status, status_name) values('ST10002', N'部門長承認待ち[A部]');
insert into fw_status_master (status, status_name) values('ST10003', N'事業部長承認待ち');
insert into fw_status_master (status, status_name) values('ST10004', N'役員承認待ち');
insert into fw_status_master (status, status_name) values('ST10005', N'決裁済み[役員]');
insert into fw_status_master (status, status_name) values('ST20000', N'Nothing');
insert into fw_status_master (status, status_name) values('ST20001', N'新規作成[B部]');
insert into fw_status_master (status, status_name) values('ST30001', N'社長承認待ち');
insert into fw_status_master (status, status_name) values('ST30002', N'決裁済み[社長]');

insert into fw_wf_rote (action_code, action, pre_status, post_status) values ('ACT10000', N'保存[A部]', 'ST10000', 'ST10001');
insert into fw_wf_rote (action_code, action, pre_status, post_status) values ('ACT10001', N'申請[A部]', 'ST10001', 'ST10002');
insert into fw_wf_rote (action_code, action, pre_status, post_status) values ('ACT10002', N'承認[A部門長]', 'ST10002', 'ST10003');
insert into fw_wf_rote (action_code, action, pre_status, post_status) values ('ACT10003', N'承認[事業部長]', 'ST10003', 'ST10004');
insert into fw_wf_rote (action_code, action, pre_status, post_status) values ('ACT10004', N'決裁[役員]', 'ST10004', 'ST10005');
insert into fw_wf_rote (action_code, action, pre_status, post_status) values ('ACT20000', N'保存[B部]', 'ST20000', 'ST20001');
insert into fw_wf_rote (action_code, action, pre_status, post_status) values ('ACT20001', N'申請[B部]', 'ST20001', 'ST10003');
insert into fw_wf_rote (action_code, action, pre_status, post_status) values ('ACT30001', N'承認[役員]', 'ST10004', 'ST30001');
insert into fw_wf_rote (action_code, action, pre_status, post_status) values ('ACT30002', N'決裁[社長]', 'ST30001', 'ST30002');

insert into fw_role_action (role, action_code) values('RLA0001','ACT10000');
insert into fw_role_action (role, action_code) values('RLA0001','ACT10001');
insert into fw_role_action (role, action_code) values('RLA0002','ACT10002');
insert into fw_role_action (role, action_code) values('RLB0001','ACT20000');
insert into fw_role_action (role, action_code) values('RLB0001','ACT20001');
insert into fw_role_action (role, action_code) values('RLX0001','ACT10003');
insert into fw_role_action (role, action_code) values('RLY0001','ACT10004');
insert into fw_role_action (role, action_code) values('RLY0001','ACT30001');
insert into fw_role_action (role, action_code) values('RLZ0001','ACT30002');
