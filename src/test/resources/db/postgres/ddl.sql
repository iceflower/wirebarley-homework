DROP TABLE IF EXISTS transactions;
DROP TABLE IF EXISTS accounts;
DROP FUNCTION IF EXISTS

CREATE TABLE accounts
(
  account_id                 BIGINT PRIMARY KEY,
  account_owner_name         VARCHAR(30)  NOT NULL CHECK (LENGTH(account_owner_name) >= 2),
  account_owner_phone_number VARCHAR(13)  NOT NULL CHECK (LENGTH(account_owner_phone_number) > 0),
  account_owner_email        VARCHAR(100) CHECK (LENGTH(account_owner_email) > 0),
  user_type                  VARCHAR(15),
  total_amount               NUMERIC(32, 6) DEFAULT 0.000000 CHECK (total_amount >= 0),
  created_at                 TIMESTAMP    NOT NULL,
  created_by                 VARCHAR(100) NOT NULL,
  updated_at                 TIMESTAMP    NOT NULL,
  updated_by                 VARCHAR(100) NOT NULL,
);

CREATE UNIQUE INDEX uk_account_owner_phone_number ON accounts (account_owner_phone_number);
CREATE UNIQUE INDEX uk_account_owner_email ON accounts (account_owner_email);

COMMENT
ON TABLE accounts IS '계좌 정보 테이블';
COMMENT
ON COLUMN accounts.account_id IS '계좌번호';
COMMENT
ON COLUMN accounts.account_owner_name IS '예금주 (2자 이상의 문자열만 입력 가능)';
COMMENT
ON COLUMN accounts.account_owner_phone_number IS '예금주 전화번호 (UNIQUE, 1자 이상의 문자열만 입력 가능)';
COMMENT
ON COLUMN accounts.account_owner_email IS '예금주 이메일 (UNIQUE, 1자 이상의 문자열만 입력 가능)';
COMMENT
ON COLUMN accounts.user_type IS '예금주 유형';
COMMENT
ON COLUMN accounts.total_amount IS '예금 잔고 (0 이상의 숫자만 입력 가능)';
COMMENT
ON COLUMN accounts.created_at IS '최초생성시각';
COMMENT
ON COLUMN accounts.created_by IS '최초생성자';
COMMENT
ON COLUMN accounts.updated_at IS '최종수정시각';
COMMENT
ON COLUMN accounts.updated_by IS '최종수정자';


CREATE TABLE transactions
(
  transaction_id      VARCHAR(13) PRIMARY KEY,
  transaction_type    VARCHAR(10)    NOT NULL,
  transaction_channel VARCHAR(24)    NOT NULL,
  origin_account_id   BIGINT,
  target_account_id   BIGINT,
  amount              NUMERIC(32, 6) NOT NULL CHECK (amount > 0),
  fee_amount          NUMERIC(32, 6) NOT NULL CHECK (fee_amount >= 0),
  created_at          TIMESTAMP      NOT NULL,
  created_by          VARCHAR(100)   NOT NULL,
  updated_at          TIMESTAMP      NOT NULL,
  updated_by          VARCHAR(100)   NOT NULL,

  CONSTRAINT fk_transactions_origin_account_id FOREIGN KEY (origin_account_id) REFERENCES accounts (account_id),
  CONSTRAINT fk_transactions_target_account_id FOREIGN KEY (target_account_id) REFERENCES accounts (account_id),
);

CREATE INDEX idx_transactions_transaction_type ON transactions (transaction_type);
CREATE INDEX idx_transactions_created_at ON transactions (created_at DESC);

COMMENT
ON TABLE transactions IS '계좌별 거래내역';
COMMENT
ON COLUMN transactions.transaction_id IS '거래 ID';
COMMENT
ON COLUMN transactions.transaction_type IS '거래유형';
COMMENT
ON COLUMN transactions.transaction_channel IS '거래발생채널';
COMMENT
ON COLUMN transactions.origin_account_id IS '송금계좌 (입금 거래일 경우 null)';
COMMENT
ON COLUMN transactions.target_account_id IS '수금계좌 (출금 거래일 경우 null)';
COMMENT
ON COLUMN transactions.amount IS '거래금액 (1 이상의 값만 입력 가능)';
COMMENT
ON COLUMN transactions.fee_amount IS '거래 수수료 (0원 이상의 값만 입력 가능)';
COMMENT
ON COLUMN transactions.created_at IS '최초생성시각';
COMMENT
ON COLUMN transactions.created_by IS '최초생성자';
COMMENT
ON COLUMN transactions.updated_at IS '최종수정시각';
COMMENT
ON COLUMN transactions.updated_by IS '최종수정자';


CREATE FUNCTION fn_tsid_milli() RETURNS BIGINT AS $$
DECLARE
  -- Milliseconds precision
  C_MILLI_PREC bigint := 10^3;
  -- Random component bit length: 22 bits
  C_RANDOM_LEN
  bigint := 2^22;
  -- TSID epoch: seconds since 2020-01-01Z
  -- extract(epoch from '2020-01-01'::date)
  C_TSID_EPOCH
  bigint := 1577836800;
BEGIN
RETURN ((floor((extract('epoch' from clock_timestamp()) - C_TSID_EPOCH) * C_MILLI_PREC) *
         C_RANDOM_LEN)::bigint) + (floor(random() * C_RANDOM_LEN)::bigint);
END $$
LANGUAGE plpgsql;
