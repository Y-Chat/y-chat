-- Users Start -------------------------------------------------------------------------------------
CREATE TABLE users (
    id UUID NOT NULL,
    -- Profile
    first_name VARCHAR(32) NOT NULL,
    last_name VARCHAR(32) NOT NULL,
    profile_description VARCHAR(128) NOT NULL,
    --Settings
    two_factor_auth BOOLEAN NOT NULL,
    read_receipts BOOLEAN NOT NULL,
    --Timestamps
    created TIMESTAMP NOT NULL,
    modified TIMESTAMP NOT NULL,

    CONSTRAINT pk_users PRIMARY KEY (id)
);

CREATE TABLE blocked_users (
    from_user_id UUID NOT NULL,
    to_user_id UUID NOT NULL,
    -- Timestamps
    created TIMESTAMP NOT NULL,
    modified TIMESTAMP NOT NULL,

    CONSTRAINT pk_blocked_users PRIMARY KEY (from_user_id, to_user_id),
    CONSTRAINT ck_blocked_users_from_neq_to CHECK (from_user_id != to_user_id),
    CONSTRAINT fk_blocked_users_users_from FOREIGN KEY (from_user_id) REFERENCES users (id),
    CONSTRAINT fk_blocked_users_users_to FOREIGN KEY (to_user_id) REFERENCES users (id)
);
-- Users End ---------------------------------------------------------------------------------------

-- Chats start -------------------------------------------------------------------------------------
CREATE TABLE chats (
    id UUID NOT NULL,
    -- Timestamps
    created TIMESTAMP NOT NULL,
    modified TIMESTAMP NOT NULL,

    CONSTRAINT pk_chats PRIMARY KEY (id)
);

CREATE TABLE direct_chats (
    id UUID NOT NULL,
    -- Timestamps
    created TIMESTAMP NOT NULL,
    modified TIMESTAMP NOT NULL,

    CONSTRAINT pk_direct_chats PRIMARY KEY (id),
    CONSTRAINT fk_direct_chats_chats FOREIGN KEY (id) REFERENCES chats (id)
);

CREATE TYPE DIRECT_CHAT_STATUS AS ENUM ('DELETED', 'ARCHIVED', 'ACTIVE');

CREATE TABLE direct_chat_members (
    chat_id UUID NOT NULL,
    user_id UUID NOT NULL,
    chat_status DIRECT_CHAT_STATUS NOT NULL,
    -- Timestamps
    created TIMESTAMP NOT NULL,
    modified TIMESTAMP NOT NULL,

    CONSTRAINT pk_direct_chat_members PRIMARY KEY (chat_id, user_id),
    CONSTRAINT fk_direct_chat_members_direct_chats FOREIGN KEY (user_id)
        REFERENCES direct_chats (id),
    CONSTRAINT fk_direct_chat_members_users FOREIGN KEY (user_id) REFERENCES users (id)
);
-- Chats end ---------------------------------------------------------------------------------------

-- Groups Start -----------------------------------------------------------------------------------
CREATE TABLE groups (
    id UUID NOT NULL,
    -- Profile
    group_name VARCHAR(32) NOT NULL,
    profile_description VARCHAR(128) NOT NULL,
    -- Timestamps
    created TIMESTAMP NOT NULL,
    modified TIMESTAMP NOT NULL,

    CONSTRAINT pk_groups PRIMARY KEY (id),
    CONSTRAINT fk_groups_chats FOREIGN KEY (id) REFERENCES chats (id)
);

CREATE TYPE GROUP_CHAT_STATUS AS ENUM ('ARCHIVED', 'ACTIVE');
CREATE TYPE GROUP_ROLE AS ENUM ('GROUP_MEMBER', 'GROUP_ADMIN');

CREATE TABLE group_members (
    group_id UUID NOT NULL,
    user_id UUID NOT NULL,
    chat_status GROUP_CHAT_STATUS NOT NULL,
    group_role GROUP_ROLE NOT NULL,
    -- Timestamps
    created TIMESTAMP NOT NULL,
    modified TIMESTAMP NOT NULL,

    CONSTRAINT pk_group_members PRIMARY KEY (group_id, user_id),
    CONSTRAINT fk_group_members_groups FOREIGN KEY (group_id) REFERENCES groups (id),
    CONSTRAINT fk_group_members_users FOREIGN KEY (user_id) REFERENCES users (id)
);
-- Groups End -------------------------------------------------------------------------------------