-- Users Start ------------------------------------------------------------------------------------
CREATE TABLE users (
    id UUID NOT NULL
    ,first_name VARCHAR(32) NOT NULL
    ,last_name VARCHAR(32) NOT NULL
    ,profile_description VARCHAR(128) NOT NULL

    ,CONSTRAINT pk_users_id PRIMARY KEY (id)
);

CREATE TABLE blocked (
    from_user_id UUID NOT NULL
    ,to_user_id UUID NOT NULL

    ,CONSTRAINT pk_blocked_from_user_id_to_user_id PRIMARY KEY (from_user_id, to_user_id)
    ,CONSTRAINT fk_blocked_users_from FOREIGN KEY (from_user_id) REFERENCES users (id)
    ,CONSTRAINT fk_blocked_users_to FOREIGN KEY (to_user_id) REFERENCES users (id)
);

CREATE TABLE direct_chat_members (
    chat_id UUID NOT NULL
    ,user_id UUID NOT NULL
    ,deleted BOOLEAN NULL
    ,archived BOOLEAN NULL

    ,CONSTRAINT pk_direct_chat_members_chat_id_user_id PRIMARY KEY (chat_id, user_id)
    ,CONSTRAINT fk_direct_chat_members_users FOREIGN KEY (user_id) REFERENCES users (id)
    -- Having both the deleted and archived fields set may confuse the business logic
    ,CONSTRAINT ck_direct_chat_members_deleted_archived CHECK (
        (deleted IS NULL AND archived IS NOT NULL)
        OR (deleted IS NOT NULL AND archived IS NULL)
    )
);
-- Users End --------------------------------------------------------------------------------------

-- Groups Start -----------------------------------------------------------------------------------
CREATE TABLE groups (
    -- Group ID serves as chat ID
    id UUID NOT NULL
    ,group_name VARCHAR(32) NOT NULL
    ,profile_description VARCHAR(128) NOT NULL

    ,CONSTRAINT pk_groups_id PRIMARY KEY (id)
);

CREATE TYPE GROUP_ROLE AS ENUM ('GROUP_MEMBER', 'GROUP_ADMIN');

CREATE TABLE group_members (
    group_id UUID NOT NULL
    ,user_id UUID NOT NULL
    ,group_role GROUP_ROLE NOT NULL
    ,chat_archived BOOLEAN NOT NULL

    ,CONSTRAINT pk_group_members_group_id_user_id PRIMARY KEY (group_id, user_id)
    ,CONSTRAINT fk_group_members_groups FOREIGN KEY (group_id) REFERENCES groups (id)
    ,CONSTRAINT fk_group_members_users FOREIGN KEY (user_id) REFERENCES user (id)
);
-- Groups End -------------------------------------------------------------------------------------