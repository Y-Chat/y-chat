/*
 Assumes the database has UTC as system time and en_US.utf8 as default collation.

 Chat status is stored as a string and not as an enum because JPA has some problems converting the
 string to the correct PostgreSQL object. My JPA version also dislikes the solution of Vlad
 Mihalcea.
 */

-- User Start --------------------------------------------------------------------------------------
CREATE TABLE IF NOT EXISTS "user" (
    id UUID NOT NULL,
    -- Profile
    first_name VARCHAR(32) COLLATE pg_catalog."default" NOT NULL,
    last_name VARCHAR(32) COLLATE pg_catalog."default" NOT NULL,
    profile_picture_id VARCHAR(1024) NULL,
    profile_description VARCHAR(128) COLLATE pg_catalog."default" NOT NULL,
    --Settings
    read_receipts BOOLEAN NOT NULL,
    last_seen BOOLEAN NOT NULL,
    --Timestamps
    created TIMESTAMP WITHOUT TIME ZONE NOT NULL,
    modified TIMESTAMP WITHOUT TIME ZONE NOT NULL,

    CONSTRAINT pk_user PRIMARY KEY (id),
    CONSTRAINT ck_user_first_name CHECK (LENGTH(first_name) >= 1),
    CONSTRAINT ck_user_last_name CHECK (LENGTH(last_name) >= 1),
    CONSTRAINT ck_user_created_modified CHECK (created <= modified)
);

CREATE TABLE IF NOT EXISTS blocked_user (
    from_user_id UUID NOT NULL,
    to_user_id UUID NOT NULL,
    -- Timestamps
    created TIMESTAMP WITHOUT TIME ZONE NOT NULL,
    modified TIMESTAMP WITHOUT TIME ZONE NOT NULL,

    CONSTRAINT pk_blocked_user PRIMARY KEY (from_user_id, to_user_id),
    CONSTRAINT fk_blocked_user_user_from FOREIGN KEY (from_user_id) REFERENCES "user" (id),
    CONSTRAINT fk_blocked_user_user_to FOREIGN KEY (to_user_id) REFERENCES "user" (id),
    CONSTRAINT ck_blocked_user_from_neq_to CHECK (from_user_id != to_user_id),
    CONSTRAINT ck_blocked_user_created_modified CHECK (created <= modified)
);
-- User End ----------------------------------------------------------------------------------------

-- Chat start --------------------------------------------------------------------------------------
CREATE TABLE IF NOT EXISTS chat (
    id UUID NOT NULL,
    -- Timestamps
    created TIMESTAMP WITHOUT TIME ZONE NOT NULL,
    modified TIMESTAMP WITHOUT TIME ZONE NOT NULL,

    CONSTRAINT pk_chat PRIMARY KEY (id),
    CONSTRAINT ck_chat_created_modified CHECK (created <= modified)
);

CREATE TABLE IF NOT EXISTS direct_chat (
    id UUID NOT NULL,
    -- Timestamps

    CONSTRAINT pk_direct_chat PRIMARY KEY (id),
    CONSTRAINT fk_direct_chat_chat FOREIGN KEY (id) REFERENCES chat (id)
);

CREATE TABLE IF NOT EXISTS direct_chat_member (
    chat_id UUID NOT NULL,
    user_id UUID NOT NULL,
    other_user_id UUID NOT NULL,
    chat_status VARCHAR(32) COLLATE pg_catalog."default" NOT NULL,
    -- Timestamps
    created TIMESTAMP WITHOUT TIME ZONE NOT NULL,
    modified TIMESTAMP WITHOUT TIME ZONE NOT NULL,

    CONSTRAINT pk_direct_chat_member PRIMARY KEY (chat_id, user_id),
    CONSTRAINT fk_direct_chat_member_direct_chat FOREIGN KEY (chat_id)
        REFERENCES direct_chat (id),
    CONSTRAINT fk_direct_chat_member_user FOREIGN KEY (user_id) REFERENCES "user" (id),
    CONSTRAINT ck_direct_chat_member_user_neq_other_user CHECK (user_id != other_user_id),
    CONSTRAINT ck_direct_chat_member_chat_status
        CHECK (chat_status IN ('DELETED', 'ARCHIVED', 'ACTIVE')),
    CONSTRAINT ck_direct_chat_member_created_modified CHECK (created <= modified)
);
-- Chat end ----------------------------------------------------------------------------------------

-- Group Start -------------------------------------------------------------------------------------
CREATE TABLE IF NOT EXISTS "group" (
    id UUID NOT NULL,
    -- Profile
    group_name VARCHAR(32) COLLATE pg_catalog."default" NOT NULL,
    profile_picture_id VARCHAR(1024) NULL,
    profile_description VARCHAR(128) COLLATE pg_catalog."default" NOT NULL,

    CONSTRAINT pk_group PRIMARY KEY (id),
    CONSTRAINT fk_group_chat FOREIGN KEY (id) REFERENCES chat (id),
    CONSTRAINT ck_group_group_name CHECK (LENGTH(group_name) >= 1)
);

CREATE TABLE IF NOT EXISTS group_member (
    chat_id UUID NOT NULL,
    user_id UUID NOT NULL,
    chat_status VARCHAR(32) COLLATE pg_catalog."default" NOT NULL,
    group_role VARCHAR(32) COLLATE pg_catalog."default" NOT NULL,
    -- Timestamps
    created TIMESTAMP WITHOUT TIME ZONE NOT NULL,
    modified TIMESTAMP WITHOUT TIME ZONE NOT NULL,

    CONSTRAINT pk_group_member PRIMARY KEY (chat_id, user_id),
    CONSTRAINT fk_group_member_group FOREIGN KEY (chat_id) REFERENCES "group" (id),
    CONSTRAINT fk_group_member_user FOREIGN KEY (user_id) REFERENCES "user" (id),
    CONSTRAINT ck_group_member_chat_status CHECK (chat_status IN ('ARCHIVED', 'ACTIVE')),
    CONSTRAINT ck_group_member_group_role CHECK (group_role IN ('GROUP_MEMBER', 'GROUP_ADMIN')),
    CONSTRAINT ck_group_member_created_modified CHECK (created <= modified)
);
-- Group End ---------------------------------------------------------------------------------------