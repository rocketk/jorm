use test;
drop table if exists `employee`;

create table `employee`
(
    `pk`                bigint unsigned not null auto_increment,
    `name`              varchar(128)    not null,
    `gender`            tinyint comment '1 for male and 0 for female',
    `academic_degree`   varchar(64) comment 'NON/BACHELOR/MASTER/DOCTORATE',
    `salary`            decimal(10, 2),
    `birth_date`        date,
    `avatar`            blob,
    `tags`              varchar(1024),
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

insert into `employee`
set `name`              = 'Jack',
    `gender`            = 1,
    `academic_degree`   = 'NON',
    `salary`            = '20000.00',
    `tags`              = 'dev t1',
    `attributes`        = '{"key1": "value1", "key2": "value2"}',
    `during_internship` = true,
    `birth_date`        = str_to_date('1988-12-31', '%Y-%m-%d'),
    `created_at`        = now(),
    `updated_at`        = now();
insert into `employee`
set `name`              = 'Benjamin',
    `gender`            = 1,
    `academic_degree`   = 'BACHELOR',
    `salary`            = '31901.51',
    `tags`              = 'hr',
    `attributes`        = '{"key1": "value1", "key2": "value2"}',
    `during_internship` = false,
    `birth_date`        = str_to_date('1989-01-01', '%Y-%m-%d'),
    `created_at`        = now(),
    `updated_at`        = now();
insert into `employee`
set `name`              = 'Mary',
    `gender`            = 0,
    `academic_degree`   = 'MASTER',
    `salary`            = '12345.09',
    `tags`              = 'manager m3',
    `attributes`        = '{"key1": "value1", "key2": "value2"}',
    `during_internship` = false,
    `birth_date`        = str_to_date('2000-10-01', '%Y-%m-%d'),
    `created_at`        = now(),
    `updated_at`        = now();
insert into `employee`
set `name`              = '王大锤',
    `gender`            = 0,
    `academic_degree`   = 'DOCTORATE',
    `salary`            = '43210.50',
    `tags`              = 'CEO',
    `attributes`        = '{"key1": "value1", "key2": "value2"}',
    `during_internship` = false,
    `birth_date`        = str_to_date('1968-04-01', '%Y-%m-%d'),
    `profile`           = '{"fullName": "王大锤", "email": "wangdachui@dachui.com", "bio": "嗨，大家好"}',
    `created_at`        = now(),
    `updated_at`        = now();