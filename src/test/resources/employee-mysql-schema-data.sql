use test;
drop table if exists `employee`;

create table `employee`
(
    `pk`                bigint unsigned not null auto_increment,
    `name`              varchar(128)    not null,
    `gender`            tinyint(1) comment '1 for male and 0 for female',
    `academic_degree`   varchar(64) comment 'NON/BACHELOR/MASTER/DOCTORATE',
    `salary`            decimal(10, 2),
    `birth_date`        date,
    `avatar`            blob,
    `tags`              text,
    `languages`         text,
    `attributes`        text,
    `during_internship` bool,
    `profile`           text,
    `created_at`        datetime        not null,
    `updated_at`        datetime        not null,
    `deleted_at`        datetime,
    primary key (`pk`)
) engine = innodb
  auto_increment = 1001
  default charset = utf8mb4;

delete
from `employee`;

insert into employee (name, gender, academic_degree, salary, birth_date, tags, languages, attributes,
                      during_internship, profile, created_at, updated_at)
values ( 'Jack', 1, 'NON', 20000.00, str_to_date('1988-12-31', '%Y-%m-%d'), 'dev t1', 'java python'
       , '{"key1": "value1", "key2": "value2"}', 0
       , '{"fullName": "Jack", "email": "jack@rocket.com", "bio": "I am Jack"}'
       , str_to_date('2022-04-07 15:03:45', '%Y-%m-%d %H:%i:%s')
       , str_to_date('2022-04-07 15:03:45', '%Y-%m-%d %H:%i:%s'));

insert into employee (name, gender, academic_degree, salary, birth_date, tags, languages, attributes,
                      during_internship, profile, created_at, updated_at)
values ( 'Benjamin', 1, 'BACHELOR', 31901.51, str_to_date('1989-01-01', '%Y-%m-%d'), 'hr', 'java python'
       , '{"key1": "value1", "key2": "value2"}', 0
       , '{"fullName": "Benjamin", "email": "benjamin@rocket.com", "bio": "Hello!"}'
       , str_to_date('2022-04-07 15:03:45', '%Y-%m-%d %H:%i:%s')
       , str_to_date('2022-04-07 15:03:45', '%Y-%m-%d %H:%i:%s'));

insert into employee (name, gender, academic_degree, salary, birth_date, tags, languages, attributes,
                      during_internship, profile, created_at, updated_at)
values ( 'Mary', 0, 'MASTER', 12345.01, str_to_date('1979-10-01', '%Y-%m-%d'), 'manager m3', 'java python'
       , '{"key1": "value1", "key2": "value2"}', 0
       , null
       , str_to_date('2022-04-07 15:03:45', '%Y-%m-%d %H:%i:%s')
       , str_to_date('2022-04-07 15:03:45', '%Y-%m-%d %H:%i:%s'));

insert into employee (name, gender, academic_degree, salary, birth_date, tags, languages, attributes,
                      during_internship, profile, created_at, updated_at)
values ( '张三', 0, 'BACHELOR', 1000.90, str_to_date('2008-01-01', '%Y-%m-%d'), 'student', null
       , null, 1
       , ''
       , str_to_date('2022-04-07 15:03:45', '%Y-%m-%d %H:%i:%s')
       , str_to_date('2022-04-07 15:03:45', '%Y-%m-%d %H:%i:%s'));
