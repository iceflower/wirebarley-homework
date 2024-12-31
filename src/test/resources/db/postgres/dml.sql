INSERT INTO accounts (account_id, account_owner_name, account_owner_phone_number, account_owner_email, user_type, total_amount, created_at, created_by, updated_at, updated_by)
values (fn_tsid_milli(), "테스트용고객", "010-123-1234", "test1@test.com", "NORMAL_CUSTOMER", 0, now(), "SYSTEM", now(), "SYSTEM")
