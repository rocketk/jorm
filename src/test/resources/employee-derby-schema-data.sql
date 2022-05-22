-- drop table employee;
create table employee
(
    pk                integer generated by default as identity (start with 1001) primary key,
    name              varchar(128) not null,
    gender            smallint,
    academic_degree   varchar(64),
    salary            decimal(10, 2),
    birth_date        date,
    avatar            blob,
    tags              long varchar,
    languages         long varchar,
    attributes        long varchar,
    during_internship smallint,
    profile           long varchar,
    created_at        timestamp    not null,
    updated_at        timestamp    not null,
    deleted_at        timestamp
);


delete
from employee;

insert into employee (name, gender, academic_degree, salary, birth_date, tags, languages, attributes,
                      during_internship, profile, created_at, updated_at)
values ( 'Jack', 1, 'NON', 20000.00, '1988-12-31', 'dev t1', 'java python'
       , '{"key1": "value1", "key2": "value2"}', 0
       , '{"fullName": "Jack Trump", "email": "jack@rocket.com", "bio": "I am Jack"}'
       , '2022-04-07-15.03.45'
       , '2022-04-07-15.03.45');

insert into employee (name, gender, academic_degree, salary, birth_date, tags, languages, attributes,
                      during_internship, profile, created_at, updated_at)
values ( 'Benjamin', 1, 'BACHELOR', 31901.51, '1989-01-01', 'hr', 'java python'
       , '{"key1": "value1", "key2": "value2"}', 0
       , '{"fullName": "Benjamin", "email": "benjamin@rocket.com", "bio": "Hello!"}'
       , '2022-04-07 15:03:45'
       , '2022-04-07 15:03:45');

insert into employee (name, gender, academic_degree, salary, birth_date, tags, languages, attributes,
                      during_internship, profile, created_at, updated_at)
values ( 'Mary', 0, 'MASTER', 12345.01, '1979-10-01', 'manager m3', 'java python'
       , '{"key1": "value1", "key2": "value2"}', 0
       , null
       , '2022-04-07 15:03:45'
       , '2022-04-07 15:03:45');

insert into employee (name, gender, academic_degree, salary, birth_date, tags, languages, attributes,
                      during_internship, profile, created_at, updated_at)
values ( '张三', 0, 'BACHELOR', 1000.90, '2008-01-01', 'student', null
       , null, 1
       , ''
       , '2022-04-07 15:03:45'
       , '2022-04-07 15:03:45');

insert into employee (name, gender, academic_degree, salary, birth_date, tags, languages, attributes,
                      during_internship, profile, created_at, updated_at, deleted_at)
values ( 'Bruce', 0, 'BACHELOR', 1000.90, '2008-01-01', 'student', null
       , null, 1
       , ''
       , '2022-04-07 15:03:45'
       , '2022-04-07 15:03:45'
       , '2022-04-13 00:00:00');
