CREATE TABLE room (
    id                      BIGINT       NOT NULL AUTO_INCREMENT,
    title                   VARCHAR(100) NOT NULL,
    deposit_won             INT          NOT NULL,
    monthly_rent_won        INT          NOT NULL,
    maintenance_fee_won     INT          NOT NULL,
    monthly_cost_won        INT          GENERATED ALWAYS AS (monthly_rent_won + maintenance_fee_won),
    move_in_date            DATE         NOT NULL,
    expiry_date             DATE         NOT NULL,
    gate                    VARCHAR(30)  NOT NULL,
    transfer_benefit        BOOLEAN      NOT NULL,
    transfer_benefit_detail TEXT,
    trade_status            VARCHAR(20)  NOT NULL,
    description             TEXT         NOT NULL,
    contact_url             VARCHAR(500) NOT NULL,
    details                 JSON         NOT NULL,
    created_at              DATETIME(6)  NOT NULL,
    updated_at              DATETIME(6)  NOT NULL,
    PRIMARY KEY (id)
);

CREATE INDEX idx_room_created_at ON room (created_at);
CREATE INDEX idx_room_gate_created_at ON room (gate, created_at);
CREATE INDEX idx_room_monthly_cost ON room (monthly_cost_won);
CREATE INDEX idx_room_deposit ON room (deposit_won);
CREATE INDEX idx_room_transfer_benefit_created_at ON room (transfer_benefit, created_at);

CREATE TABLE room_image (
    id            BIGINT        NOT NULL AUTO_INCREMENT,
    room_id       BIGINT        NOT NULL,
    url           VARCHAR(2048) NOT NULL,
    display_order INT           NOT NULL,
    PRIMARY KEY (id),
    CONSTRAINT fk_room_image_room FOREIGN KEY (room_id) REFERENCES room (id)
);

CREATE INDEX idx_room_image_room_order ON room_image (room_id, display_order);

CREATE TABLE survey_feedback (
    id             CHAR(36)    NOT NULL,
    answer         TEXT        NOT NULL,
    reason         TEXT,
    viewed_listings INT        NOT NULL,
    created_at     DATETIME(6) NOT NULL,
    PRIMARY KEY (id)
);
