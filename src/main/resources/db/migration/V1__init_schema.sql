-- V1__init_schema.sql

CREATE TABLE user_accounts (
    id          UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    cognito_sub VARCHAR(255) NOT NULL UNIQUE,
    email       VARCHAR(255) NOT NULL,
    display_name VARCHAR(255),
    created_at  TIMESTAMP NOT NULL
);

CREATE TABLE categories (
    id              UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    owner_id        UUID NOT NULL REFERENCES user_accounts(id) ON DELETE CASCADE,
    name            VARCHAR(255) NOT NULL,
    type            VARCHAR(50)  NOT NULL,
    is_default      BOOLEAN NOT NULL DEFAULT FALSE
);

CREATE INDEX idx_categories_owner ON categories(owner_id);

CREATE TABLE expenses (
    id          UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    owner_id    UUID NOT NULL REFERENCES user_accounts(id) ON DELETE CASCADE,
    category_id UUID NOT NULL REFERENCES categories(id),
    amount      NUMERIC(19, 4) NOT NULL,
    currency    CHAR(3) NOT NULL,
    description VARCHAR(500),
    date        DATE NOT NULL,
    status      VARCHAR(50) NOT NULL DEFAULT 'ACTIVE'
);

CREATE INDEX idx_expenses_owner        ON expenses(owner_id);
CREATE INDEX idx_expenses_owner_status ON expenses(owner_id, status);
CREATE INDEX idx_expenses_date         ON expenses(owner_id, date DESC);

