CREATE TABLE asp_task
(
    id            BIGINT        NOT NULL,
    max_points    NUMERIC(7, 2) NOT NULL,
    status        TASK_STATUS   NOT NULL,
    task_group_id BIGINT        NOT NULL,
    solution      TEXT          NOT NULL,
    max_n         INT,
    CONSTRAINT asp_task_pk PRIMARY KEY (id),
    CONSTRAINT asp_task_task_group_fk FOREIGN KEY (task_group_id) REFERENCES task_group (id)
        ON DELETE CASCADE
);

CREATE TABLE asp_submission
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
    CONSTRAINT asp_submission_pk PRIMARY KEY (id),
    CONSTRAINT asp_submission_task_fk FOREIGN KEY (task_id) REFERENCES asp_task (id)
        ON DELETE CASCADE
);
