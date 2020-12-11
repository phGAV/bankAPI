DROP TABLE IF EXISTS Cards;
DROP TABLE IF EXISTS Accounts;
DROP TABLE IF EXISTS Clients;

CREATE TABLE IF NOT EXISTS Clients (
    id          IDENTITY NOT NULL PRIMARY KEY,
    name        VARCHAR(255) NOT NULL
);

CREATE TABLE IF NOT EXISTS Accounts (
    number      BIGINT NOT NULL PRIMARY KEY CHECK(number > 1E15 AND number < 1E16),
    balance     DECIMAL(20, 2) NOT NULL DEFAULT 0,
    owner_id    BIGINT NOT NULL REFERENCES Clients(id)
);

CREATE TABLE IF NOT EXISTS Cards (
    card_number BIGINT NOT NULL PRIMARY KEY CHECK(card_number > 1E15 AND card_number < 1E16),
    account_id  BIGINT NOT NULL REFERENCES Accounts(number)
);

