-- !!! DO NOT MODIFY THIS MIGRATION !!!
-- IF YOU WANT TO ADD/CHANGE SOMETHING CREATE A NEW MIGRATION, e.g. V2__add_column_to_table.sql

CREATE TYPE task_status AS ENUM ('draft', 'ready_for_approval', 'approved');
CREATE TYPE submission_mode AS ENUM ('run', 'diagnose', 'submit');
CREATE TYPE grading_strategy AS ENUM ('each', 'group', 'ko');

CREATE CAST (CHARACTER VARYING as task_status) WITH INOUT AS IMPLICIT;
CREATE CAST (CHARACTER VARYING as submission_mode) WITH INOUT AS IMPLICIT;
CREATE CAST (CHARACTER VARYING as grading_strategy) WITH INOUT AS IMPLICIT;

CREATE TABLE task_group
(
    id               BIGINT      NOT NULL,
    status           TASK_STATUS NOT NULL,
    diagnose_facts   TEXT        NOT NULL,
    submission_facts TEXT        NOT NULL,
    CONSTRAINT task_group_pk PRIMARY KEY (id)
);

CREATE TABLE task
(
    id                           BIGINT           NOT NULL,
    max_points                   NUMERIC(7, 2)    NOT NULL,
    status                       TASK_STATUS      NOT NULL,
    task_group_id                BIGINT           NOT NULL,
    solution                     TEXT             NOT NULL,
    query                        TEXT[]           NOT NULL,
    unchecked_term_raw           TEXT,
    unchecked_terms              JSONB, -- Intentionally redundant with unchecked_term_raw
    missing_predicate_penalty    NUMERIC(5, 2)    NOT NULL DEFAULT 0,
    missing_predicate_strategy   grading_strategy NOT NULL DEFAULT 'ko',
    missing_fact_penalty         NUMERIC(5, 2)    NOT NULL DEFAULT 0,
    missing_fact_strategy        grading_strategy NOT NULL DEFAULT 'ko',
    superfluous_fact_penalty       NUMERIC(5, 2)    NOT NULL DEFAULT 0,
    superfluous_fact_strategy      grading_strategy NOT NULL DEFAULT 'ko',
    CONSTRAINT task_pk PRIMARY KEY (id),
    CONSTRAINT task_task_group_fk FOREIGN KEY (task_group_id) REFERENCES task_group (id)
        ON DELETE CASCADE,
    CONSTRAINT task_missing_predicate_penalty_ck CHECK (missing_predicate_penalty >= 0),
    CONSTRAINT task_missing_fact_penalty_ck CHECK (missing_fact_penalty >= 0),
    CONSTRAINT task_superfluous_fact_penalty_ck CHECK (superfluous_fact_penalty >= 0)
);

CREATE TABLE submission
(
    id                UUID                     DEFAULT gen_random_uuid(),
    user_id           VARCHAR(255),
    assignment_id     VARCHAR(255),
    task_id           BIGINT,
    submission_time   TIMESTAMP       NOT NULL DEFAULT CURRENT_TIMESTAMP,
    language          VARCHAR(2)      NOT NULL DEFAULT 'en',
    mode              submission_mode NOT NULL,
    feedback_level    INT             NOT NULL,
    evaluation_result JSONB,
    submission        TEXT            NOT NULL,
    CONSTRAINT submission_pk PRIMARY KEY (id),
    CONSTRAINT submission_task_fk FOREIGN KEY (task_id) REFERENCES task (id)
        ON DELETE CASCADE
);
