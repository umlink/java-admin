CREATE TABLE IF NOT EXISTS sys_user
(
    id         BIGINT PRIMARY KEY AUTO_INCREMENT,
    username   VARCHAR(64)  NOT NULL,
    password   VARCHAR(128) NOT NULL,
    nickname   VARCHAR(64)           DEFAULT NULL,
    phone      VARCHAR(20)           DEFAULT NULL,
    email      VARCHAR(128)          DEFAULT NULL,
    status     TINYINT      NOT NULL DEFAULT 1,
    deleted    TINYINT      NOT NULL DEFAULT 0,
    created_at DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    created_by BIGINT                DEFAULT NULL,
    updated_by BIGINT                DEFAULT NULL,
    UNIQUE KEY uk_username (username)
);

CREATE TABLE IF NOT EXISTS sys_role
(
    id         BIGINT PRIMARY KEY AUTO_INCREMENT,
    role_code  VARCHAR(64) NOT NULL,
    role_name  VARCHAR(64) NOT NULL,
    status     TINYINT     NOT NULL DEFAULT 1,
    remark     VARCHAR(255)         DEFAULT NULL,
    deleted    TINYINT     NOT NULL DEFAULT 0,
    created_at DATETIME    NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at DATETIME    NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    created_by BIGINT               DEFAULT NULL,
    updated_by BIGINT               DEFAULT NULL,
    UNIQUE KEY uk_role_code (role_code)
);

CREATE TABLE IF NOT EXISTS sys_menu
(
    id              BIGINT PRIMARY KEY AUTO_INCREMENT,
    parent_id       BIGINT      NOT NULL DEFAULT 0,
    menu_name       VARCHAR(64) NOT NULL,
    menu_type       TINYINT     NOT NULL,
    path            VARCHAR(128)         DEFAULT NULL,
    component       VARCHAR(128)         DEFAULT NULL,
    permission_code VARCHAR(128)         DEFAULT NULL,
    sort_num        INT         NOT NULL DEFAULT 0,
    status          TINYINT     NOT NULL DEFAULT 1,
    deleted         TINYINT     NOT NULL DEFAULT 0,
    created_at      DATETIME    NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at      DATETIME    NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
);

CREATE TABLE IF NOT EXISTS sys_user_role
(
    id         BIGINT PRIMARY KEY AUTO_INCREMENT,
    user_id    BIGINT   NOT NULL,
    role_id    BIGINT   NOT NULL,
    created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    UNIQUE KEY uk_user_role (user_id, role_id)
);

CREATE TABLE IF NOT EXISTS sys_role_menu
(
    id         BIGINT PRIMARY KEY AUTO_INCREMENT,
    role_id    BIGINT   NOT NULL,
    menu_id    BIGINT   NOT NULL,
    created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    UNIQUE KEY uk_role_menu (role_id, menu_id)
);