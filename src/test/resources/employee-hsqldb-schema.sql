drop table if exists employee;
create table employee
(
    pk                bigint not null auto_increment,
    name              varchar(128)    not null,
    gender            tinyint(1) comment '1 for male and 0 for female',
    academic_degree   varchar(64) comment 'NON/BACHELOR/MASTER/DOCTORATE',
    salary            decimal(10, 2),
    birth_date        date,
    avatar            blob,
    tags              text,
    languages         text,
    attributes        text,
    during_internship bool,
    profile           text,
    created_at        datetime        not null,
    updated_at        datetime        not null,
    deleted_at        datetime,
    primary key (pk)
) engine = innodb
  auto_increment = 1001
  default charset = utf8mb4;
