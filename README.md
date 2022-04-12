### 说明

JORM是一个基于JDBC的轻量级ORM工具。与Mybatis和Hibernate不同的是，JORM鼓励开发者直接利用SQL来对数据进行操作，其目的在于帮助开发者更好地发挥SQL的能力，而像字段映射、类型转换、拼接SQL字符串等这一类枯燥的工作则交给JORM来自动完成。

**本项目当前还处于早期阶段，不建议在生产环境中使用**

### 查询
#### 添加条件语句和查询参数
```java
@Test
public void testQueryFirst_withChineseChar() {
  final Jorm db = new Jorm(ds);
  final Optional<Employee> employee = db.query(Employee.class).where("name=?", "赵今麦").first();
  // sql: "select * from employee  where name=?  fetch first 1 rows only", args: "[赵今麦]"
  assertTrue(employee.isPresent());
  final Employee zjm = employee.get();
}
```

#### 仅查询指定的列

有些表会存在很多列，但是每次查询仅用到其中的少量几个列，那么如果能够在查询中精确指定你所需要的列，有可能会很大程度上提高程序的查询性能，通过使用`select()`方法来实现这一点：

```java
@Test
public void testQueryFirst_withSelect() {
  final Jorm db = new Jorm(ds);
  final Optional<Employee> employee = db.query(Employee.class)
    .select("pk", "name", "gender")
    .where("name=?", "Jack")
    .first();
  // sql: "select gender,name,pk from employee  where name=?  fetch first 1 rows only", args: "[Jack]"
  assertTrue(employee.isPresent());
  final Employee jack = employee.get();
}
```



#### 返回单条记录与多条记录

上面展示了用`first()`方法来返回单条记录，下面展示`find()`方法返回多条记录：

```java
@Test
public void testQueryFind() {
  final Jorm db = new Jorm(ds);
  final List<Employee> list = db.query(Employee.class).find();
  // sql: "select * from employee "
  assertNotNull(list);
  assertEquals(4, list.size());
}
```



#### 隐藏指定的列或字段

某些场景下，你会希望隐藏某一列中的值，你可以使用`omit()`方法来达到这一目的：

```java
@Test
public void testQueryFirst_withOmit() {
  final Jorm db = new Jorm(ds);
  final Optional<Employee> employee = db.query(Employee.class).omit("profile").where("name=?", "Jack").first();
  // sql: "select * from employee  where name=?  fetch first 1 rows only", args: "[Jack]"
  assertTrue(employee.isPresent());
  final Employee jack = employee.get();
  assertNull(jack.getProfile());
}
```

不过要注意的是，如果不使用`select()`，仅仅使用`omit()`的话，那么SQL中的列仍然会是`*`，也就是说JDBC依然会返回全部的列，而JORM只是在从`ResultSet`向`Employee`做字段映射时，才会屏蔽掉`omit()`所制定的列，在上例中就是`profile`。

#### 排序、分页

#### 字段名与列名、类型与表名的自动映射和自定义映射

#### 仅查询未删除的记录

#### 自动类型转换

##### 常规类型

##### 枚举类型

##### 数组类型

##### Json类型

### 待完成功能：

- [X] 插入
  - [X] 自动设置`created_at`和`updated_at`
- [X] 更新
  - [X] 自动设置`updated_at`
- [ ] 删除
  - [ ] 软删除
- [ ] 查询
  - [ ] 自定义映射
- [ ] 批量插入和更新
- [ ] 事务