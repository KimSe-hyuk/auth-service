CREATE DATABASE IF NOT EXISTS catalog
    DEFAULT CHARACTER SET utf8mb4
    DEFAULT COLLATE utf8mb4_unicode_ci;

CREATE TABLE IF NOT EXISTS member (
    id             int auto_increment primary key,
    user_id        varchar(255) not null unique,
    user_name      varchar(100) not null,
    password       varchar(255) null,
    nick_name      varchar(255) not null unique,
    email_provider varchar(255) null,
    email          varchar(255) not null unique,
    role           varchar(255) not null
    ) DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

