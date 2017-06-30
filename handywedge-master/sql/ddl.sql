
/* Drop Tables */

DROP TABLE IF EXISTS fw_role_action;
DROP TABLE IF EXISTS fw_action;
DROP TABLE IF EXISTS fw_api_token;
DROP TABLE IF EXISTS fw_notice;
DROP TABLE IF EXISTS fw_role_acl;
DROP TABLE IF EXISTS fw_user_management;
DROP TABLE IF EXISTS fw_user_passwd;
DROP TABLE IF EXISTS fw_user_passwd_reset;
DROP TABLE IF EXISTS fw_user;




/* Create Tables */

-- アクション : ワークフローのステータス遷移を定義するテーブルです。
CREATE TABLE fw_action
(
    -- アクションコード
    action_code varchar(16) NOT NULL,
    -- アクション名
    action varchar(256),
    -- アクション前ステータス
    pre_status varchar(256) NOT NULL,
    -- アクション後ステータス
    post_status varchar(256) NOT NULL,
    -- 作成日時
    create_date timestamp DEFAULT CURRENT_TIMESTAMP NOT NULL,
    -- 更新日時
    update_date timestamp DEFAULT CURRENT_TIMESTAMP NOT NULL,
    CONSTRAINT pk_fw_action PRIMARY KEY (action_code)
) WITHOUT OIDS;


-- APIトークン
CREATE TABLE fw_api_token
(
    -- ユーザーID
    id varchar(128) NOT NULL,
    -- トークン
    token varchar(32) NOT NULL,
    -- 作成日時
    create_date timestamp DEFAULT CURRENT_TIMESTAMP NOT NULL,
    CONSTRAINT pk_fw_api_token PRIMARY KEY (token)
) WITHOUT OIDS;


-- 通知
CREATE TABLE fw_notice
(
    -- 通知ID
    id int NOT NULL,
    -- 通知内容
    notice varchar(4000),
    -- 作成日時
    create_date timestamp DEFAULT CURRENT_TIMESTAMP NOT NULL,
    -- 更新日時
    update_date timestamp DEFAULT CURRENT_TIMESTAMP NOT NULL,
    CONSTRAINT pk_fw_notice PRIMARY KEY (id)
) WITHOUT OIDS;


-- ロール別ACL
CREATE TABLE fw_role_acl
(
    -- ロール
    role varchar(256) NOT NULL,
    -- URLパターン
    url_pattern varchar(256) NOT NULL,
    -- 作成日時
    create_date timestamp DEFAULT CURRENT_TIMESTAMP NOT NULL,
    -- 更新日時
    update_date timestamp DEFAULT CURRENT_TIMESTAMP NOT NULL
) WITHOUT OIDS;


-- ロール別アクション : ロール別のアクション制御を定義するテーブルです。
CREATE TABLE fw_role_action
(
    -- ロール
    role varchar(256) NOT NULL,
    -- アクションコード
    action_code varchar(16) NOT NULL,
    -- 作成日時
    create_date timestamp DEFAULT CURRENT_TIMESTAMP NOT NULL,
    -- 更新日時
    update_date timestamp DEFAULT CURRENT_TIMESTAMP NOT NULL,
    CONSTRAINT pk_fw_role_action PRIMARY KEY (role, action_code)
) WITHOUT OIDS;


-- ユーザー
CREATE TABLE fw_user
(
    -- ユーザーID
    id varchar(128) NOT NULL,
    -- 名前
    name varchar(256),
    -- メールアドレス
    mail_address varchar(256),
    -- ロール
    role varchar(256),
    -- 国 : jpなどのLocale国コード
    country varchar(64),
    -- 言語 : jaなどのLocale言語情報
    language varchar(64),
    -- 最終ログイン日時
    last_login_date timestamp,
    -- 作成日時
    create_date timestamp DEFAULT CURRENT_TIMESTAMP NOT NULL,
    -- 更新日時
    update_date timestamp DEFAULT CURRENT_TIMESTAMP NOT NULL,
    CONSTRAINT pk_fw_user PRIMARY KEY (id)
) WITHOUT OIDS;


-- ユーザー管理
CREATE TABLE fw_user_management
(
    -- ユーザーID
    id varchar(128) NOT NULL,
    -- 仮登録
    pre_register boolean DEFAULT '1' NOT NULL,
    -- 仮登録トークン
    pre_token varchar(32) UNIQUE,
    -- 作成日時
    create_date timestamp DEFAULT CURRENT_TIMESTAMP NOT NULL,
    -- 更新日時
    update_date timestamp DEFAULT CURRENT_TIMESTAMP NOT NULL,
    CONSTRAINT pk_fw_user_management PRIMARY KEY (id)
) WITHOUT OIDS;


-- ユーザーパスワード
CREATE TABLE fw_user_passwd
(
    -- ユーザーID
    id varchar(128) NOT NULL,
    -- パスワード : blowfishでハッシュ化されたもの
    passwd varchar(64) NOT NULL,
    -- 作成日時
    create_date timestamp DEFAULT CURRENT_TIMESTAMP NOT NULL,
    -- 更新日時
    update_date timestamp DEFAULT CURRENT_TIMESTAMP NOT NULL,
    CONSTRAINT pk_fw_user_passwd PRIMARY KEY (id)
) WITHOUT OIDS;


-- パスワードリセット管理
CREATE TABLE fw_user_passwd_reset
(
    -- ユーザーID
    id varchar(128) NOT NULL,
    -- リセットトークン
    reset_token varchar(32) NOT NULL UNIQUE,
    -- 作成日時
    create_date timestamp DEFAULT CURRENT_TIMESTAMP NOT NULL,
    CONSTRAINT pk_fw_user_passwd_reset PRIMARY KEY (id)
) WITHOUT OIDS;



/* Create Foreign Keys */

ALTER TABLE fw_role_action
    ADD CONSTRAINT fk_fw_role_action FOREIGN KEY (action_code)
    REFERENCES fw_action (action_code)
    ON UPDATE NO ACTION
    ON DELETE NO ACTION
;


ALTER TABLE fw_api_token
    ADD CONSTRAINT fk_fw_api_token FOREIGN KEY (id)
    REFERENCES fw_user (id)
    ON UPDATE NO ACTION
    ON DELETE NO ACTION
;


ALTER TABLE fw_user_management
    ADD CONSTRAINT fk_fw_user_management FOREIGN KEY (id)
    REFERENCES fw_user (id)
    ON UPDATE NO ACTION
    ON DELETE NO ACTION
;


ALTER TABLE fw_user_passwd
    ADD CONSTRAINT fk_fw_user_passwd FOREIGN KEY (id)
    REFERENCES fw_user (id)
    ON UPDATE NO ACTION
    ON DELETE NO ACTION
;


ALTER TABLE fw_user_passwd_reset
    ADD CONSTRAINT fk_fw_user_passwd_reset FOREIGN KEY (id)
    REFERENCES fw_user (id)
    ON UPDATE NO ACTION
    ON DELETE NO ACTION
;



