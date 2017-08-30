CREATE TABLE test(
    key     VARCHAR(255) NOT NULL,
    value   VARCHAR(255),
    CONSTRAINT pk_test PRIMARY KEY (key)
);

CREATE TABLE workflow(
    id SERIAL NOT NULL,
    subject VARCHAR(100),
    apply_user_id VARCHAR(64),
    status VARCHAR(64),
    create_date timestamp DEFAULT CURRENT_TIMESTAMP NOT NULL,
    update_date timestamp DEFAULT CURRENT_TIMESTAMP NOT NULL,
    CONSTRAINT pk_workflow PRIMARY KEY (id)
);

CREATE TABLE workflow2(
    id SERIAL NOT NULL,
    subject VARCHAR(100),
    body VARCHAR(200),
    wf_id varchar(36),
    create_date timestamp DEFAULT CURRENT_TIMESTAMP NOT NULL,
    update_date timestamp DEFAULT CURRENT_TIMESTAMP NOT NULL,
    CONSTRAINT pk_workflow2 PRIMARY KEY (id)
);
