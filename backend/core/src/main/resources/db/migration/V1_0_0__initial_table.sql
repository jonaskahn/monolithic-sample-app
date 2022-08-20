CREATE TABLE IF NOT EXISTS authorities
(
    id                 BIGINT auto_increment PRIMARY KEY,
    created_by         VARCHAR(255) NULL,
    created_date       DATETIME(6) NULL,
    last_modified_by   VARCHAR(255) NULL,
    last_modified_date datetime(6) NULL,
    description        VARCHAR(500) NULL,
    name               VARCHAR(100) NULL,
    status             INT(1) NULL
    );

CREATE TABLE IF NOT EXISTS `groups`
(
    id                 BIGINT auto_increment PRIMARY KEY,
    created_by         VARCHAR(255) NULL,
    created_date       DATETIME(6) NULL,
    last_modified_by   VARCHAR(255) NULL,
    last_modified_date datetime(6) NULL,
    description        VARCHAR(500) NULL,
    domain             VARCHAR(100) NULL,
    email              VARCHAR(80) NULL,
    name               VARCHAR(100) NULL,
    status             INT(1) NULL
    );

CREATE TABLE IF NOT EXISTS roles
(
    id                 BIGINT auto_increment PRIMARY KEY,
    created_by         VARCHAR(255) NULL,
    created_date       DATETIME(6) NULL,
    last_modified_by   VARCHAR(255) NULL,
    last_modified_date datetime(6) NULL,
    description        VARCHAR(500) NULL,
    name               VARCHAR(100) NULL,
    status             INT(1) NULL,
    parent_id          BIGINT NULL,
    CONSTRAINT uk_roles_name UNIQUE (name),
    CONSTRAINT fk_roles__roles FOREIGN KEY (parent_id) REFERENCES roles (id)
    );

CREATE TABLE IF NOT EXISTS roles_authorities
(
    role_id      BIGINT NOT NULL,
    authority_id BIGINT NOT NULL,
    PRIMARY KEY (role_id, authority_id),
    CONSTRAINT fk_roles_authorities__roles FOREIGN KEY (role_id) REFERENCES roles (id),
    CONSTRAINT fk_roles_authorities__authorities FOREIGN KEY (authority_id) REFERENCES authorities (id)
    );

CREATE TABLE IF NOT EXISTS users
(
    id                    BIGINT auto_increment PRIMARY KEY,
    created_by            VARCHAR(255) NULL,
    created_date          DATETIME(6) NULL,
    last_modified_by      VARCHAR(255) NULL,
    last_modified_date    datetime(6) NULL,
    birthdate             date NULL,
    email                 VARCHAR(255) NOT NULL,
    email_verified        INT(1) NULL,
    status              INT(1) NULL,
    family_name           VARCHAR(255) NULL,
    gender                INT NULL,
    given_name            VARCHAR(255) NULL,
    middle_name           VARCHAR(255) NULL,
    name                  VARCHAR(255) NULL,
    password              VARCHAR(255) NULL,
    phone_number          VARCHAR(255) NULL,
    phone_number_verified INT(1) NULL,
    preferred_username    VARCHAR(255) NULL,
    unsigned_name         VARCHAR(255) NULL,
    username              VARCHAR(255) NOT NULL,
    CONSTRAINT uk_users_username UNIQUE (username),
    CONSTRAINT uk_users_email UNIQUE (email)
    );

CREATE TABLE IF NOT EXISTS group_members
(
    user_id  BIGINT NOT NULL,
    group_id BIGINT NOT NULL,
    PRIMARY KEY (user_id, group_id),
    CONSTRAINT fk_group_members__groups FOREIGN KEY (group_id) REFERENCES `groups` (id),
    CONSTRAINT fk_group_members__users FOREIGN KEY (user_id) REFERENCES users (id)
    );

CREATE TABLE IF NOT EXISTS user_roles
(
    user_id BIGINT NOT NULL,
    role_id BIGINT NOT NULL,
    PRIMARY KEY (user_id, role_id),
    CONSTRAINT fk_user_roles__roles FOREIGN KEY (role_id) REFERENCES roles (id),
    CONSTRAINT fk_user_roles__users FOREIGN KEY (user_id) REFERENCES users (id)
    );

CREATE TABLE access_tokens
      (
          id      VARCHAR(255) NOT NULL PRIMARY KEY,
          user_id BIGINT NOT NULL,
          expired_at DATETIME(6) NULL,
          status     INT(1) NULL,
          CONSTRAINT fk_access_tokens__users FOREIGN KEY (user_id) REFERENCES users (id)
      );

CREATE TABLE refresh_tokens
        (
            id              VARCHAR(255) NOT NULL PRIMARY KEY,
            access_token_id VARCHAR(255) NOT NULL,
            expired_at DATETIME(6) NULL,
            status     INT(1) NULL,
            CONSTRAINT fk_refresh_tokens__access_tokens FOREIGN KEY (access_token_id) REFERENCES access_tokens (id)
        );