
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
/*
insert into fw_action (action_code, action, pre_status, post_status) values ('00102', N'申請取消し', N'上長承認待ち', N'新規作成');
insert into fw_role_action (role, action_code) values(N'担当者','00102');
*/

-- 新WFテスト用
insert into fw_user (id, name, role, country, language, last_login_date)  values('wf201', N'WFユーザー１', 'RL00001', 'jp', 'ja', null);
insert into fw_user (id, name, role, country, language, last_login_date)  values('wf202', N'WFユーザー２', 'RL00002', 'jp', 'ja', null);
insert into fw_user (id, name, role, country, language, last_login_date)  values('wf203', N'WFユーザー３', 'RL00003', 'jp', 'ja', null);
insert into fw_user (id, name, role, country, language, last_login_date)  values('wf204', N'WFユーザー４', 'RL00004', 'jp', 'ja', null);
-- パスワード＝「password」
insert into fw_user_passwd (id, passwd) values('wf201', '$2a$10$1x/sImsfWiVTJqUbrkbSa.PNTxuwSlTjeAZ.n3/HN651xj20.9DFG');
insert into fw_user_passwd (id, passwd) values('wf202', '$2a$10$1x/sImsfWiVTJqUbrkbSa.PNTxuwSlTjeAZ.n3/HN651xj20.9DFG');
insert into fw_user_passwd (id, passwd) values('wf203', '$2a$10$1x/sImsfWiVTJqUbrkbSa.PNTxuwSlTjeAZ.n3/HN651xj20.9DFG');
insert into fw_user_passwd (id, passwd) values('wf204', '$2a$10$1x/sImsfWiVTJqUbrkbSa.PNTxuwSlTjeAZ.n3/HN651xj20.9DFG');

insert into fw_role_master (role, role_name) values('RL00001', N'担当者');
insert into fw_role_master (role, role_name) values('RL00002', N'上長');
insert into fw_role_master (role, role_name) values('RL00003', N'部門長');
insert into fw_role_master (role, role_name) values('RL00004', N'事業部長');

-- 通常フロー検証用
insert into fw_status_master (status, status_name) values('ST00000', N'Nothing');
insert into fw_status_master (status, status_name) values('ST00001', N'新規作成');
insert into fw_status_master (status, status_name) values('ST00002', N'上長承認待ち');
insert into fw_status_master (status, status_name) values('ST00003', N'部門長承認待ち');
insert into fw_status_master (status, status_name) values('ST00004', N'決済済み');

insert into fw_wf_rote (action_code, action, pre_status, post_status) values ('ACT00100', N'保存', 'ST00000', 'ST00001');
insert into fw_wf_rote (action_code, action, pre_status, post_status) values ('ACT00101', N'申請', 'ST00001', 'ST00002');
insert into fw_wf_rote (action_code, action, pre_status, post_status) values ('ACT00201', N'承認（上長）', 'ST00002', 'ST00003');
insert into fw_wf_rote (action_code, action, pre_status, post_status) values ('ACT00301', N'承認（部門長）', 'ST00003', 'ST00004');

insert into fw_role_action (role, action_code) values('RL00001','ACT00100');
insert into fw_role_action (role, action_code) values('RL00001','ACT00101');
insert into fw_role_action (role, action_code) values('RL00002','ACT00100');
insert into fw_role_action (role, action_code) values('RL00002','ACT00101');
insert into fw_role_action (role, action_code) values('RL00002','ACT00201');
insert into fw_role_action (role, action_code) values('RL00003','ACT00301');

-- 合流フロー検証用
insert into fw_status_master (status, status_name) values('STA0000', N'A:Nothing');
insert into fw_status_master (status, status_name) values('STA0001', N'A:新規作成');
insert into fw_status_master (status, status_name) values('STA0002', N'A:上長承認待ち');
insert into fw_status_master (status, status_name) values('STA0003', N'A:部門長承認待ち');
insert into fw_status_master (status, status_name) values('STA0004', N'A:事業部長承認待ち');
insert into fw_status_master (status, status_name) values('STA0005', N'A:決済済み');
insert into fw_status_master (status, status_name) values('STB0000', N'B:Nothing');
insert into fw_status_master (status, status_name) values('STB0001', N'B:新規作成');
insert into fw_status_master (status, status_name) values('STB0002', N'B:上長承認待ち');
insert into fw_status_master (status, status_name) values('STB0003', N'B:部門長承認待ち');

insert into fw_wf_rote (action_code, action, pre_status, post_status) values ('ACTA0100', N'A:保存', 'STA0000', 'STA0001');
insert into fw_wf_rote (action_code, action, pre_status, post_status) values ('ACTA0101', N'A:申請', 'STA0001', 'STA0002');
insert into fw_wf_rote (action_code, action, pre_status, post_status) values ('ACTA0201', N'A:承認（上長）', 'STA0002', 'STA0003');
insert into fw_wf_rote (action_code, action, pre_status, post_status) values ('ACTA0301', N'A:承認（部門長）', 'STA0003', 'STA0004');
insert into fw_wf_rote (action_code, action, pre_status, post_status) values ('ACTA0401', N'A:承認（事業部長）', 'STA0004', 'STA0005');
insert into fw_wf_rote (action_code, action, pre_status, post_status) values ('ACTB0100', N'B:保存', 'STB0000', 'STB0001');
insert into fw_wf_rote (action_code, action, pre_status, post_status) values ('ACTB0101', N'B:申請', 'STB0001', 'STB0002');
insert into fw_wf_rote (action_code, action, pre_status, post_status) values ('ACTB0201', N'B:承認（上長）', 'STB0002', 'STA0003');

insert into fw_role_action (role, action_code) values('RL00001','ACTA0100');
insert into fw_role_action (role, action_code) values('RL00001','ACTA0101');
insert into fw_role_action (role, action_code) values('RL00002','ACTA0201');
insert into fw_role_action (role, action_code) values('RL00003','ACTA0301');
insert into fw_role_action (role, action_code) values('RL00004','ACTA0401');
insert into fw_role_action (role, action_code) values('RL00001','ACTB0100');
insert into fw_role_action (role, action_code) values('RL00001','ACTB0101');
insert into fw_role_action (role, action_code) values('RL00002','ACTB0201');

-- 分岐フロー検証用
insert into fw_status_master (status, status_name) values('STC0000', N'C:Nothing');
insert into fw_status_master (status, status_name) values('STC0001', N'C:新規作成');
insert into fw_status_master (status, status_name) values('STC0002', N'C:上長承認待ち');
insert into fw_status_master (status, status_name) values('STC0003', N'C:部門長承認待ち');
insert into fw_status_master (status, status_name) values('STC0004', N'C:事業部長承認待ち');
insert into fw_status_master (status, status_name) values('STC0005', N'C:決済済み');
insert into fw_status_master (status, status_name) values('STD0003', N'D:部門長承認待ち');
insert into fw_status_master (status, status_name) values('STD0004', N'D:事業部長承認待ち');
insert into fw_status_master (status, status_name) values('STD0005', N'D:決済済み');

insert into fw_wf_rote (action_code, action, pre_status, post_status) values ('ACTC0100', N'C:保存', 'STC0000', 'STC0001');
insert into fw_wf_rote (action_code, action, pre_status, post_status) values ('ACTC0101', N'C:申請', 'STC0001', 'STC0002');
insert into fw_wf_rote (action_code, action, pre_status, post_status) values ('ACTC0201', N'C:承認（上長）', 'STC0002', 'STC0003');
insert into fw_wf_rote (action_code, action, pre_status, post_status) values ('ACTC0301', N'C:承認（部門長）', 'STC0003', 'STC0004');
insert into fw_wf_rote (action_code, action, pre_status, post_status) values ('ACTC0401', N'C:承認（事業部長）', 'STC0004', 'STC0005');
insert into fw_wf_rote (action_code, action, pre_status, post_status) values ('ACTD0201', N'D:承認（上長）', 'STC0002', 'STD0003');
insert into fw_wf_rote (action_code, action, pre_status, post_status) values ('ACTD0301', N'D:承認（部門長）', 'STD0003', 'STD0004');
insert into fw_wf_rote (action_code, action, pre_status, post_status) values ('ACTD0401', N'D:承認（事業部長）', 'STD0004', 'STD0005');

insert into fw_role_action (role, action_code) values('RL00001','ACTC0100');
insert into fw_role_action (role, action_code) values('RL00001','ACTC0101');
insert into fw_role_action (role, action_code) values('RL00002','ACTC0201');
insert into fw_role_action (role, action_code) values('RL00003','ACTC0301');
insert into fw_role_action (role, action_code) values('RL00004','ACTC0401');
insert into fw_role_action (role, action_code) values('RL00002','ACTD0201');
insert into fw_role_action (role, action_code) values('RL00003','ACTD0301');
insert into fw_role_action (role, action_code) values('RL00004','ACTD0401');
