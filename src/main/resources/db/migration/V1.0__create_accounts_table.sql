CREATE TABLE IF NOT EXISTS accounts
(
	iban VARCHAR(30),
    name VARCHAR(50),
    surname VARCHAR(50),
    address_street VARCHAR(50),
    address_house_number VARCHAR(10),
    address_post_code VARCHAR(10),
    address_city VARCHAR(50),
    date_of_birth VARCHAR(20),
    id_document_type VARCHAR(15),
    id_document_number INT,
    id_document_country_code VARCHAR(50),
    id_document_issue_date VARCHAR(20),
    id_document_expiry_date VARCHAR(20),
    account_balance DECIMAL(15,4),
    type_of_account VARCHAR(7),
    password VARCHAR(50),
    username VARCHAR(20),
    PRIMARY KEY (iban)
);

CREATE INDEX acc_user_idx ON accounts (username);

CREATE TABLE IF NOT EXISTS users
(
    password VARCHAR(50),
    username VARCHAR(20),
    PRIMARY KEY (username)
);