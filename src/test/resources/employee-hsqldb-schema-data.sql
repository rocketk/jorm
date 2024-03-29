drop table if exists employee;
create table employee
(
    pk                integer generated by default as identity (start with 1001) primary key,
    name              varchar(128) not null,
    gender            tinyint,
    academic_degree   varchar(64),
    salary            decimal(10, 2),
    birth_date        date,
    avatar            blob,
    tags              longvarchar,
    languages         longvarchar,
    attributes        longvarchar,
    during_internship tinyint,
    profile           longvarchar,
    created_at        datetime     not null,
    updated_at        datetime     not null,
    deleted_at        datetime
);


delete
from employee;

insert into employee (name, gender, academic_degree, salary, birth_date, tags, languages, attributes,
                      during_internship, profile, created_at, updated_at)
values ( 'Jack', 1, 'NON', 20000.00, to_date('1988-12-31', 'YYYY-MM-DD'), 'dev t1', 'java python'
       , '{"key1": "value1", "key2": "value2"}', 0
       , '{"fullName": "Jack Trump", "email": "jack@rocket.com", "bio": "I am Jack"}'
       , to_timestamp('2022-04-07 15:03:45', 'YYYY-MM-DD HH:MI:SS')
       , to_timestamp('2022-04-07 15:03:45', 'YYYY-MM-DD HH:MI:SS'));

insert into employee (name, gender, academic_degree, salary, birth_date, tags, languages, attributes,
                      during_internship, profile, created_at, updated_at)
values ( 'Benjamin', 1, 'BACHELOR', 31901.51, to_date('1989-01-01', 'YYYY-MM-DD'), 'hr', 'java python'
       , '{"key1": "value1", "key2": "value2"}', 0
       , '{"fullName": "Benjamin", "email": "benjamin@rocket.com", "bio": "Hello!"}'
       , to_timestamp('2022-04-07 15:03:45', 'YYYY-MM-DD HH:MI:SS')
       , to_timestamp('2022-04-07 15:03:45', 'YYYY-MM-DD HH:MI:SS'));

insert into employee (name, gender, academic_degree, salary, birth_date, tags, languages, attributes,
                      during_internship, profile, created_at, updated_at)
values ( 'Mary', 0, 'MASTER', 12345.01, to_date('1979-10-01', 'YYYY-MM-DD'), 'manager m3', 'java python'
       , '{"key1": "value1", "key2": "value2"}', 0
       , null
       , to_timestamp('2022-04-07 15:03:45', 'YYYY-MM-DD HH:MI:SS')
       , to_timestamp('2022-04-07 15:03:45', 'YYYY-MM-DD HH:MI:SS'));

insert into employee (name, gender, academic_degree, salary, birth_date, tags, languages, attributes,
                      during_internship, profile, created_at, updated_at)
values ( '韩梅梅', 0, 'BACHELOR', 1000.90, to_date('2008-01-01', 'YYYY-MM-DD'), 'student', null
       , null, 1
       , ''
       , to_timestamp('2022-04-07 15:03:45', 'YYYY-MM-DD HH:MI:SS')
       , to_timestamp('2022-04-07 15:03:45', 'YYYY-MM-DD HH:MI:SS'));

insert into employee (name, gender, academic_degree, salary, birth_date, tags, languages, attributes,
                      during_internship, profile, created_at, updated_at, deleted_at)
values ( 'Elizabeth', 0, 'BACHELOR', 1000.90, to_date('2008-01-01', 'YYYY-MM-DD'), 'student', null
       , null, 1
       , ''
       , to_timestamp('2022-04-07 15:03:45', 'YYYY-MM-DD HH:MI:SS')
       , to_timestamp('2022-04-07 15:03:45', 'YYYY-MM-DD HH:MI:SS')
       , to_timestamp('2022-04-13 00:00:00', 'YYYY-MM-DD HH:MI:SS'));
