[![Maven Central](https://img.shields.io/maven-central/v/io.github.rocketk/jorm.svg?label=Maven%20Central)](https://search.maven.org/search?q=g:%22io.github.rocketk%22%20AND%20a:%22jorm%22)
[![Issues](https://img.shields.io/github/issues/rocketk/jorm)](https://github.com/rocketk/jorm/issues)
[![Forks](https://img.shields.io/github/forks/rocketk/jorm)](https://github.com/rocketk/jorm/issues)
[![Stars](https://img.shields.io/github/stars/rocketk/jorm)](https://github.com/rocketk/jorm/issues)
[![License](https://img.shields.io/github/license/rocketk/jorm)](https://github.com/rocketk/jorm/issues)

[中文](README-CN.md)

# Introduction

JORM is a lightweight JDBC-based ORM tool.  
Different from Mybatis and Hibernate, JORM encourages developers to directly use SQL to operate data, JORM will handle the other boring works automatically.

# Features

- [x] Base CRUD
- [x] Raw SQL
- [x] Soft Delete
- [x] Automatic Type Conversion
  - [x] Array & List
  - [x] JSON
  - [x] Blob
  - [x] Enum
    - [x] Use Default Value of a Enum Object
    - [x] Custom Value of a Enum Object
  - [x] BigDecimal
  - [x] String
  - [x] Base Types
- [x] Auto generation of `created_at` and `updated_at`
- [x] Custom `RowMapper`
- [ ] Batch
- [x] Transaction

# Get Started

## Install

Just add the dependency to your `pom.xml`

```xml
<dependency>
    <groupId>io.github.rocketk</groupId>
    <artifactId>jorm</artifactId>
    <version>1.0.5</version>
</dependency>
```

## Create JROM object

A Jorm object is the API entry for all operations on a database, it only needs a `javax.sql.DataSource`.

**Standard SQL**
Supported Databases:
- Oracle
- HsqlDB
- Other Databases that support the features of `fetch first n rows only` and `offset n`. See `io.github.rocketk.jorm.dialect.StandardLimitOffsetAppender`
```java
// ignored the creation stage of the dataSource
final Jorm db = new Jorm(dataSource);
```

**Mysql**

```java
final Config config = ConfigFactory.defaultConfig();
config.setDialect(Dialect.MYSQL);
final Jorm db = new Jorm(dataSource, config);
```

**Derby**

```java
// 省略创建 dataSource 的步骤
final Config config = ConfigFactory.defaultConfig();
config.setDialect(Dialect.DERBY);
final Jorm db = new Jorm(dataSource, config);
```



## Query
### Where clause
```java
final Optional<Employee> employee = db.query(Employee.class).where("name=?", "Jack").first();
// sql: "select * from employee  where name=?  fetch first 1 rows only", args: "[Jack]"
final Employee jack = employee.get();
```

### Query only specified columns

Some tables have many columns, but only a few of them are used in each query. If you can specify exactly the columns you need in the query, it may greatly improve the performance of the program. By using `select()` method to get it:
```java
final Jorm db = new Jorm(ds);
final Optional<Employee> employee = db.query(Employee.class)
    .select("pk", "name", "gender")
    .where("name=?", "Jack")
    .first();
// sql: "select gender,name,pk from employee  where name=?  fetch first 1 rows only", args: "[Jack]"
final Employee jack = employee.get();
```

### Return multiple records

The above code shows that how to use the `first()` method to return a single record, and the following shows that how to use the `find()` method to return multiple records:
```java
final Jorm db = new Jorm(ds);
final List<Employee> list = db.query(Employee.class).find();
// sql: "select * from employee "
```

### Hide the specified column or field

In some cases, you want to hide the value of a column, you can use the `omit()` method to get it:
```java
final Jorm db = new Jorm(ds);
final Optional<Employee> employee = db.query(Employee.class).omit("profile").where("name=?", "Jack").first();
// sql: "select * from employee  where name=?  fetch first 1 rows only", args: "[Jack]"
final Employee jack = employee.get();
```

However, it should be noted that if `omit()` is used without using `select()`, the columns clause in SQL will still be `*`, which means that JDBC will still return all values of all columns. But JORM will omit the mapping of columns specified in `omit()` arguments.

### `limit` `offset` `order by`

```java
final Jorm db = createJorm();
final List<Employee> list = db.query(Employee.class)
    .limit(2)
    .offset(1)
    .orderBy("pk asc")
    .find();
// sql for standard: "select * from employee  where deleted_at is null  order by pk asc  offset 1 fetch first 2 rows only"
// sql for mysql: "select * from employee  where deleted_at is null  order by pk asc  limit 2  offset 1 "
```

### Raw SQL
```java
final Jorm db = createJorm();
final Optional<Employee> elizabeth = db.rawQuery(Employee.class, "select * from employee where name=? fetch 1 row only", "Elizabeth")
        .first();
// sql for standard: "select * from employee where name=? fetch 1 row only"
// sql for mysql: "select * from employee where name=? limit 1"
```

### Mapping of field names to column names & class names to table names

JORM provides a variety of mapping schemes

#### Annotation-based mapping

Use `JormTable(name = "table_name")` to specify table name.  

Use `JormColumn(name = "column_name")` to specify column name.  

#### Automatic calculation

see `io.github.rocketk.jorm.mapper.column.SnakeCamelColumnFieldNameMapper`:

```java
public class SnakeCamelColumnFieldNameMapper implements ColumnFieldNameMapper {
    /**
     * examples:
     * "null" -> "null"
     * "" -> ""
     * "a" -> "a"
     * "some_function" -> "someFunction"
     * "app_i_d" -> "appID"
     * "redirect_u_r_i" -> "redirectURI"
     * "redirect_uri" -> "redirectUri"
     *
     * @param columnName
     * @return
     */
    @Override
    public String columnNameToFieldName(String columnName) {
        if (StringUtils.isBlank(columnName)) {
            return columnName;
        }
        return CaseFormat.LOWER_UNDERSCORE.to(CaseFormat.LOWER_CAMEL, columnName);
    }

    /**
     * examples:
     * "null" -> "null"
     * "" -> ""
     * "a" -> "a"
     * "someFunction" -> "some_function"
     * "appID" -> "app_i_d"
     * "redirectURI" -> "redirect_u_r_i"
     * "redirectUri" -> "redirect_uri"
     *
     * @param fieldName
     * @return
     */
    @Override
    public String fieldNameToColumnName(String fieldName) {
        if (StringUtils.isBlank(fieldName)) {
            return fieldName;
        }
        return CaseFormat.LOWER_CAMEL.to(CaseFormat.LOWER_UNDERSCORE, fieldName);
    }
}
```

### Query with the "soft delete" feature turned on

This feature is only meaningful when you enable the "soft delete" feature. To enable "soft delete", you need to add `@JormTable(enableSoftDelete() == true)` on an Entity class.

When a Java class is marked as "soft delete", when querying it (corresponding table), only undeleted records are queried by default.  
Look the following code, here we assume that the user `Elizabeth` has been marked "deleted", i.e. `deleted_at is not null`.  
The `Employee` class in the code below is marked with `@JormTable(enableSoftDelete() == true)`, while `Employee2` is not, then the following assertion will be true:
```java
@Test
public void testQuery_withSoftDeleteEnabled() {
    final Jorm db = createJorm();
    assertFalse(db.query(Employee.class).where("name=?", "Elizabeth").first().isPresent());
    // sql: "select * from employee  where deleted_at is null  and name=?  fetch first 1 rows only", args: "[Elizabeth]"
    
    // Although you have turned on the "soft delete" feature, but sometimes you may still want to query these deleted records for a reason.
    assertTrue(db.query(Employee.class).where("name=?", "Elizabeth").shouldFindDeletedRows(true).first().isPresent());
    // sql: "select * from employee  where name=?  fetch first 1 rows only", args: "[Elizabeth]"
    
    assertTrue(db.query(Employee2.class).where("name=?", "Elizabeth").first().isPresent());
    // sql: "select * from employee  where name=?  fetch first 1 rows only", args: "[Elizabeth]"
    
    assertTrue(db.query(Employee2.class).where("name=?", "Elizabeth").shouldFindDeletedRows(false).first().isPresent());
    // sql: "select * from employee  where name=?  fetch first 1 rows only", args: "[Elizabeth]"
}
```

```java
@JormTable(name = "employee", enableSoftDelete = true)
public class Employee {
    // fields...
}
```

```java
@JormTable(name = "employee", enableSoftDelete = false)
public class Employee2 {
    // fields...
}
```

### Automatic type conversion
#### Enum
There are some variables, you want to use enum types in the Java program, but there is no enum type in the database.  
At this time, you can use the following two ways to solve the problem.
##### Default Enum value
Suppose you have the enum type `AcademicDegree` representing the degree of an employee, and its type is defined as follows:
```java
public enum AcademicDegree {
    NON,
    BACHELOR,
    MASTER,
    DOCTORATE
}
```

Jorm will call the `name()` method of the type field as its actual value in the database, and call the `Enum.valueOf()` method to convert the text value in the database to Enum object in Java.
This way does not require you to write additional conversion methods, but the disadvantage is that the database must use a string type to store this field, such as `varchar` or `char`.
If you want to store in non-string form, e.g. integer, then you can look at the second way, Custom Enum Value.

##### Custom Enum value
Suppose you have an enum type `Gender` representing the gender of an employee, and its type is defined as follows:
```java
@JormCustomEnum
public enum Gender {
    FEMALE(0),
    MALE(1);
    private final int value;

    Gender(int value) {
        this.value = value;
    }

    public int getValue() {
        return value;
    }

    public static Gender parse(Object rawValue) {
        Integer value = (Integer) rawValue;
        for (Gender d : Gender.values()) {
            if (d.value == value) {
                return d;
            }
        }
        throw new IllegalArgumentException(String.format("no such value '%d' for Gender", value));
    }
}
```
You can see that this enum type has an annotation `@JormCustomEnum`, and 2 methods: `int getValue()` and `static Gender parse(Object rawValue)`. These two methods actually correspond to the two processes of "Java enum object -> database storage value" and "database storage value -> Java enum object".

In above example, we use the numeric type as the storage type in the database (the actual storage type in the database may be `int` `tinyint`, etc.). Compared to saving enumeration type literals, the numbers obviously take up less space.

Of course, you can also use any other type rather than numeric types, just change the return type of `getValue()`.

The names of `getValue()` and `parse(Object rawValue)` can also be modified, see `JormCustomEnum.valueMethod` and `JormCustomEnum.parseMethod`
#### Array & List

When a field is of type array or List, Jorm will automatically complete the conversion for it during database query/update operations. The default implementation is `DelimiterBasedStringArrayColumnFieldMapper`, such as Java string array `['a', 'b' , 'c']` will be converted to the database string `a b c`, the default separator is space, you can change it to any character you want.

#### Json

When the type definition of a field contains `@JormJsonObject`, then Jorm will automatically complete Json serialization and deserialization for it during database query/update operations (the field type in the database is a string, which can be `text` `varchar`, etc.), such as the following class

```java
@JormJsonObject
public class Profile {
  private String fullName;
  private String email;
  private String bio;
}
```

```java
// table `employee`
public class Employee {
    // `profile` text
    private Profile profile;
    // other fields...
}
```

## Mutation (Insert, Update, Delete)
### Insert
```java
final Jorm db = createJorm();
final Employee e = new Employee();
e.setName("test");
boolean success = db.mutation(Employee.class).omit("pk").obj(e).insert();
// sql: "insert into employee (name,created_at,updated_at ) values ( ?,?,? )", argValues: "[test, 2022-06-20T11:16:16.059+0800, 2022-06-20T11:16:16.059+0800]"

boolean success = db.mutation(Employee.class).omit("pk")
        .set("name", "test2")
        .set("created_at", new Date())
        .set("updated_at", new Date())
        .insert();
// sql: "insert into employee (name,created_at,updated_at ) values ( ?,?,? )", argValues: "[test2, 2022-06-20T11:16:16.070+0800, 2022-06-20T11:16:16.070+0800]"
```

### Insert and return keys
```java
final Jorm db = createJorm();
final Employee e = new Employee();
e.setName("test");
final Date now = new Date();
e.setUpdatedAt(now);
final long pk = db.mutation(Employee.class).omit("pk").obj(e).set("created_at", now).insertAndReturnFirstKey();
// sql: "insert into employee (created_at,name,updated_at ) values ( ?,?,? )", argValues: "[2022-06-20T11:19:48.545+0800, test, 2022-06-20T11:19:48.545+0800]"
final Optional<Employee> retrieved = db.query(Employee.class).where("pk=?", pk).first();
// sql: "select * from employee  where deleted_at is null  and pk=?  fetch first 1 rows only", args: "[1006]"
```

### Update
```java
final Jorm db = createJorm();
final long affected = db.mutation(Employee.class)
        .set("academic_degree", AcademicDegree.MASTER)
        .where("name=?", "韩梅梅")
        .update();
// sql: "update employee set academic_degree=?,updated_at=? where deleted_at is null  and name=? ", argValues: "[MASTER, 2022-06-20T11:24:28.549+0800, 韩梅梅]"
final Optional<Employee> retrieved = db.query(Employee.class).where("name=?", "韩梅梅").first();
// sql: "select * from employee  where deleted_at is null  and name=?  fetch first 1 rows only", args: "[韩梅梅]"
```

### Soft Delete
```java
final Jorm db = createJorm();
final long affected = db.mutation(Employee.class).where("name=?", "Jack").delete();
// sql: "update employee set deleted_at=? where deleted_at is null  and name=? ", argValues: "[2022-06-20T14:55:03.011+0800, Jack]"
```

### Hard Delete
```java
final Jorm db = createJorm();
final long affected = db.mutation(Employee2.class).where("name=?", "Jack").delete();
// sql: "delete from employee where name=? ", argValues: "[Jack]"
```

### Where-Clause-Absent Warning
```java
final Jorm db = createJorm();
final long affected = db.mutation(Employee.class).delete(); // throws WhereClauseAbsentException
```

To disable the Where-Clause-Absent warning:
```java
final Jorm db = createJorm();
final long affected = db.mutation(Employee.class).ignoreNoWhereClauseWarning(true).delete();
// sql: "update employee set deleted_at=? where deleted_at is null ", argValues: "[2022-06-20T15:04:17.502+0800]"
```


## Transaction
### With default error handler
```java
final Jorm db = createJorm();
final BigDecimal jackNewSalary = new BigDecimal("654321.00");
final BigDecimal benjaminNewSalary = new BigDecimal("123456.00");
final boolean success = db.transaction().operations(t -> {
    t.mutation(Employee.class).where("name=?", "Jack").set("salary", jackNewSalary).update();
    t.mutation(Employee.class).where("name=?", "Benjamin").set("salary", benjaminNewSalary).update();
}).commit();
```
There is a default error handler when `.onError((t, e) -> {})` absent. It will roll back the transaction when an error occurred.  
See `io.github.rocketk.jorm.Transaction`:
```java
private BiConsumer<Transaction, Exception> onErrorFunc = (t, e) -> {
    logger.error(e.getMessage(), e);
    t.rollback();
};
```
### With custom error handler
```java
final Jorm db = createJorm();
final BigDecimal jackNewSalary = new BigDecimal("654321.00");
final BigDecimal benjaminNewSalary = new BigDecimal("123456.00");
final boolean success = db.transaction().operations(t -> {
    t.mutation(Employee.class).where("name=?", "Jack").set("salary", jackNewSalary).update();
    t.mutation(Employee.class).where("name=?", "Benjamin").set("salary1", benjaminNewSalary).update(); // throws an exception, 'salary1' is invalid
}).onError((t, e) -> {
    // do something
    System.out.println("operation failed, caused by: " + e.getMessage());
    t.rollback();
}).commit();
```
# Supported Databases
- MySQL
- HSQLDB
- Derby

In the project, there are unit test codes for `HsqlDB` and `Derby`, as well as integration test code for `Mysql` (that is, the Mysql instance is provided externally).  
The test code for other popular databases will be gradually added in the future.
