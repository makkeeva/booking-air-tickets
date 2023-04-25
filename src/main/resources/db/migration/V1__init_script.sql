CREATE TABLE authority
(
    id   BIGINT AUTO_INCREMENT NOT NULL,
    name VARCHAR(50)           NOT NULL,
    CONSTRAINT pk_authority PRIMARY KEY (id)
);

ALTER TABLE authority
    ADD CONSTRAINT uc_authority_name UNIQUE (name);

CREATE TABLE user
(
    username VARCHAR(50)      NOT NULL,
    password VARCHAR(100)     NOT NULL,
    date     date             NOT NULL,
    enabled  BIT(1) DEFAULT 0 NULL,
    CONSTRAINT pk_user PRIMARY KEY (username)
);

CREATE TABLE user_authorities
(
    authority_id  BIGINT      NOT NULL,
    user_username VARCHAR(50) NOT NULL,
    CONSTRAINT pk_user_authorities PRIMARY KEY (authority_id, user_username)
);

ALTER TABLE user
    ADD CONSTRAINT uc_user_password UNIQUE (password);

ALTER TABLE user_authorities
    ADD CONSTRAINT fk_useaut_on_authority FOREIGN KEY (authority_id) REFERENCES authority (id);

ALTER TABLE user_authorities
    ADD CONSTRAINT fk_useaut_on_user FOREIGN KEY (user_username) REFERENCES user (username);

CREATE TABLE profile
(
    id       BIGINT AUTO_INCREMENT NOT NULL,
    username VARCHAR(50)           NULL,
    name     VARCHAR(255)          NULL,
    surname  VARCHAR(255)          NULL,
    email    VARCHAR(255)          NULL,
    CONSTRAINT pk_profile PRIMARY KEY (id)
);

ALTER TABLE profile
    ADD CONSTRAINT FK_PROFILE_ON_USERNAME FOREIGN KEY (username) REFERENCES user (username);

CREATE TABLE ticket
(
    id              BIGINT AUTO_INCREMENT NOT NULL,
    arrival_point   VARCHAR(255)          NULL,
    departure_point VARCHAR(255)          NULL,
    cost            DOUBLE                NULL,
    airline         VARCHAR(255)          NULL,
    departure_time  datetime              NULL,
    arrival_time    datetime              NULL,
    CONSTRAINT pk_ticket PRIMARY KEY (id)
);

CREATE TABLE booking
(
    id        BIGINT AUTO_INCREMENT NOT NULL,
    username  VARCHAR(50)           NOT NULL,
    ticket_id BIGINT                NOT NULL,
    name      VARCHAR(255)          NULL,
    surname   VARCHAR(255)          NULL,
    passport  VARCHAR(255)          NULL,
    address   VARCHAR(255)          NULL,
    CONSTRAINT pk_booking PRIMARY KEY (id)
);

ALTER TABLE booking
    ADD CONSTRAINT FK_BOOKING_ON_TICKET FOREIGN KEY (ticket_id) REFERENCES ticket (id);

ALTER TABLE booking
    ADD CONSTRAINT FK_BOOKING_ON_USERNAME FOREIGN KEY (username) REFERENCES user (username);