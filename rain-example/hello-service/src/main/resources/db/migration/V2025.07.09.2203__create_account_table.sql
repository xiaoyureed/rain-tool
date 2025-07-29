-- Create Account Table
create table account (
    id varchar(32) primary key,
    username varchar(50),
    password varchar(200),
    phone varchar(50),
    enabled int default 1,
    created_at timestamp,
    updated_at timestamp,
    created_by varchar(32),
    updated_by varchar(32),
    deleted int default 0
);

create unique index idx_unique_account_username on account(username);
create unique index idx_unique_account_phone on account(phone);

-- insert test data
-- insert into account values('aaa', 'admin', 'admin', 1);
