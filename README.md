# 说明

JORM是一个基于JDBC的轻量级ORM工具。与Mybatis和Hibernate不同的是，JORM鼓励开发者直接利用SQL来对数据进行操作，其目的在于帮助开发者更好地发挥SQL的能力，而像字段映射、类型转换、拼接SQL字符串等这一类枯燥的工作则交给JORM来自动完成。

**本项目当前还处于早期阶段，不建议在生产环境中使用**

# 本框架的功能点

- [x] 基础的CRUD操作
- [x] 原生SQL
- [x] 软删除
- [x] 自动类型转换
  - [x] 数组和List
  - [x] JSON
  - [x] Blob
  - [x] 枚举
    - [x] 默认枚举值
    - [x] 自定义枚举值
  - [x] BigDecimal
  - [x] 字符串
  - [x] 基本类型等
- [x] 更新时自动生成`created_at` `updated_at`
- [x] 自定义`RowMapper`
- [ ] 批量插入和更新
- [ ] 事务
# 创建Jorm对象

一个Jorm对象是对数据库进行所有操作的API入口，它仅需要一个`DataSource`，这里我们推荐使用阿里巴巴的`DruidDataSource`。

**标准Sql**

```java
// 省略创建 dataSource 的步骤
final Jorm db new Jorm(dataSource);
```

**Mysql**

```java
// 省略创建 dataSource 的步骤
final Config config = ConfigFactory.defaultConfig();
config.setDialect(Dialect.MYSQL);
final Jorm db new Jorm(dataSource, config);
```

**Derby**

```java
// 省略创建 dataSource 的步骤
final Config config = ConfigFactory.defaultConfig();
config.setDialect(Dialect.DERBY);
final Jorm db new Jorm(dataSource, config);
```



# 查询
## 添加条件语句和查询参数
```java
final Optional<Employee> employee = db.query(Employee.class).where("name=?", "张三").first();
// sql: "select * from employee  where name=?  fetch first 1 rows only", args: "[张三]"
final Employee zjm = employee.get();
}
```

## 仅查询指定的列

有些表会存在很多列，但是每次查询仅用到其中的少量几个列，那么如果能够在查询中精确指定你所需要的列，有可能会很大程度上提高程序的查询性能，通过使用`select()`方法来实现这一点：

```java
final Jorm db = new Jorm(ds);
final Optional<Employee> employee = db.query(Employee.class)
    .select("pk", "name", "gender")
    .where("name=?", "Jack")
    .first();
// sql: "select gender,name,pk from employee  where name=?  fetch first 1 rows only", args: "[Jack]"
final Employee jack = employee.get();
```



## 返回单条记录与多条记录

上面展示了用`first()`方法来返回单条记录，下面展示`find()`方法返回多条记录：

```java
final Jorm db = new Jorm(ds);
final List<Employee> list = db.query(Employee.class).find();
// sql: "select * from employee "
```



## 隐藏指定的列或字段

某些场景下，你会希望隐藏某一列中的值，你可以使用`omit()`方法来达到这一目的：

```java
final Jorm db = new Jorm(ds);
final Optional<Employee> employee = db.query(Employee.class).omit("profile").where("name=?", "Jack").first();
// sql: "select * from employee  where name=?  fetch first 1 rows only", args: "[Jack]"
final Employee jack = employee.get();
```

不过要注意的是，如果不使用`select()`，仅仅使用`omit()`的话，那么SQL中的列仍然会是`*`，也就是说JDBC依然会返回全部的列，而JORM只是在从`ResultSet`向`Employee`做字段映射时，才会屏蔽掉`omit()`所制定的列，在上例中就是`profile`。

## `limit` `offset` `order by`

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

## 字段名与列名、类型与表名的映射

JORM提供了多种方式的映射方案

### 基于注解的映射

使用`JormTable(name = "table_name")`来指定表名。  

使用`JormColumn(name = "column_name")`来指定列名。  

### 基于字段名和列名自动计算出对应的列名和字段名

参见`com.github.rocketk.jorm.mapper.column.SnakeCamelColumnFieldNameMapper`:

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



## 「软删除」功能开启下的查询

这个功能是当你开启了「软删除」功能后才有意义，开启「软删除」需要使用`@JormTable`注解，并且`enableSoftDelete() == true`且`deletedAtColumn`非空。  

当一个Java类被标记为「软删除」后，那么在对它（对应的表）进行查询操作时，默认仅查询未删除的记录，例如下面的代码，这里我们假设`李四`这个用户在数据库中已被标记为删除，即`deleted_at is not null`，下面代码中`Employee`类被标记为启用「软删除」，而`Employee2`则没有，那么下面的断言将会成立：

```java
@Test
public void testQuery_withSoftDeleteEnabled() {
    final Jorm db = createJorm();
    assertFalse(db.query(Employee.class).where("name=?", "李四").first().isPresent());
    // sql: "select * from employee  where deleted_at is null  and name=?  fetch first 1 rows only", args: "[李四]"
    
    // 有时候尽管你开启了「软删除」功能，但你仍希望在某些场合下查询到这些已删除的记录
    assertTrue(db.query(Employee.class).where("name=?", "李四").shouldFindDeletedRows(true).first().isPresent());
    // sql: "select * from employee  where name=?  fetch first 1 rows only", args: "[李四]"
    
    assertTrue(db.query(Employee2.class).where("name=?", "李四").first().isPresent());
    // sql: "select * from employee  where name=?  fetch first 1 rows only", args: "[李四]"
    
    assertTrue(db.query(Employee2.class).where("name=?", "李四").shouldFindDeletedRows(false).first().isPresent());
    // sql: "select * from employee  where name=?  fetch first 1 rows only", args: "[李四]"
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

有时候尽管你开启了「软删除」功能，但你仍希望在某些场合下查询到这些已删除的记录，你可以用`shouldFindDeletedRows(true)`来强制返回已删除的记录，如上面的例子中所演示的一样。

## 自动类型转换
### 枚举类型
有一些变量，你希望在Java程序中使用枚举类型，但数据库中却没有枚举类型，这个时候你可以使用下面两种方式来建立这种映射关系
#### 默认枚举值
假设你有枚举类型`AcademicDegree`表示员工的学位，它的类型定义如下：
```java
public enum AcademicDegree {
    NON,
    BACHELOR,
    MASTER,
    DOCTORATE
}
```
Jorm会调用该类型字段的`name()`方法，即字面量，来作为它在数据库中的实际值；而从数据库中还原这个枚举类型则是调用了`Enum.valueOf()`方法。  
这种方式不需要你写额外的转换方法，但缺点就是要求数据库中必须使用字符串类型来保存此字段，例如`varchar`或`char`。  
如果你希望以非字符串的形式来存储，那么你可以看看下面这种方式。
#### 自定义枚举值
假设你有枚举类型`Gender`表示员工的性别，它的类型定义如下：
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
可以看到这个枚举类型多了一个注解`@JormCustomEnum`，和2个方法，分别是`int getValue()`和`static Gender parse(Object rawValue)`。这两个方法其实分别对应着「Java枚举对象->数据库存储值」和「数据库存储值->Java枚举对象」这两个过程。

我们可以看到，在这个例子中，我们使用数字类型来作为数据库中的存储类型（数据库中实际的存储类型也许是`int` `tinyint`等），这样做的好处是，相比于保存枚举类型的字面量，数字显然占用空间更小。

当然你也可以不用数字类型，实际上，`getValue()`可以返回任意类型。

`getValue()`与`parse(Object rawValue)`这两个方法名也是可以修改的，参见`JormCustomEnum.valueMethod`和`JormCustomEnum.parseMethod`
### 数组类型

当一个字段的类型是数组或List时，那么Jorm会在数据库存取操作时自动为它完成转换工作，默认采取的方式是分隔符法，例如Java字符串数组`['a', 'b', 'c']`会被转换成数据库字符串`a b c`，默认是以空格为分隔符的，你可以修改为任意你希望的字符。

### Json类型

当一个字段的类型定义中，含有`@JormJsonObject`时，那么Jorm会在数据库存取操作时自动为它完成Json序列化与反序列化（数据库中的字段类型为字符串，可以是`text` `varchar`等等），例如下面这个类

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

# 适配的数据库

在项目中，有针对`HsqlDB`和`Derby`的单元测试代码，也有针对`Mysql`的集成测试代码（即Mysql实例是由程序外部提供）。以后会逐步增加其它流行数据库的测试代码。