package com.github.rocketk.jorm;

import com.alibaba.druid.pool.DruidDataSource;
import com.github.rocketk.AcademicDegree;
import com.github.rocketk.BaseDataTest;
import com.github.rocketk.data.Employee;
import com.github.rocketk.data.Gender;
import com.github.rocketk.data.Profile;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import java.io.IOException;
import java.math.BigDecimal;
import java.sql.SQLException;
import java.util.*;

import static com.github.rocketk.util.DateUtil.toDate;
import static com.github.rocketk.util.DateUtil.toDateTime;
import static org.junit.Assert.*;

/**
 * @author pengyu
 * @date 2022/4/6
 */
public class JormHsqldbTest {
    private static DruidDataSource ds;

    @BeforeClass
    public static void initDataSource() throws SQLException, IOException {
        ds = BaseDataTest.createDataSourceForHsqlDb();
    }

    @AfterClass
    public static void closeDataSource() {
        if (ds != null) {
            ds.close();
        }
    }

    @Before
    public void initTestSchemaAndData() throws SQLException, IOException {
        BaseDataTest.runScript(ds, "employee-hsqldb-schema-data.sql");
    }

    @Test
    public void testInsert() {
        final Jorm db = new Jorm(ds);
        final Employee e = new Employee();
        e.setName("test");
        assertTrue(db.update(Employee.class).omit("pk").obj(e).execInsert());
        assertTrue(db.update(Employee.class).omit("pk")
                .set("name", "test2")
                .set("created_at", new Date())
                .set("updated_at", new Date())
                .execInsert());
    }

    @Test
    public void testInsertAndReturnFirstKey() {
        final Jorm db = new Jorm(ds);
        final Employee e = new Employee();
        e.setName("test");
        final long pk = db.update(Employee.class).omit("pk").obj(e).execInsertAndReturnFirstKey();
        assertTrue(pk > 0);
        final Optional<Employee> retrieved = db.query(Employee.class).where("pk=?", pk).first();
        assertTrue(retrieved.isPresent());
        assertEquals(e.getName(), retrieved.get().getName());
        assertNotNull(retrieved.get().getUpdatedAt());
        assertNotNull(retrieved.get().getCreatedAt());
        assertNull(retrieved.get().getAvatar());
        assertNull(retrieved.get().getBirthDate());
        assertNull(retrieved.get().getSalary());
        assertNull(retrieved.get().getTags());
        assertNull(retrieved.get().getDeletedAt());
    }

    @Test
    public void testInsertAndReturnFirstKey_overrideCreatedAtUpdatedAt() {
        final Jorm db = new Jorm(ds);
        final Employee e = new Employee();
        e.setName("test");
        final Date now = new Date();
        e.setUpdatedAt(now);
        final long pk = db.update(Employee.class).omit("pk").obj(e).set("created_at", now).execInsertAndReturnFirstKey();
        assertTrue(pk > 0);
        final Optional<Employee> retrieved = db.query(Employee.class).where("pk=?", pk).first();
        assertTrue(retrieved.isPresent());
        assertEquals(e.getName(), retrieved.get().getName());
        assertNotNull(retrieved.get().getUpdatedAt());
        assertNotNull(retrieved.get().getCreatedAt());
        assertEquals(now.getTime(), retrieved.get().getCreatedAt().getTime());
        assertEquals(now.getTime(), retrieved.get().getUpdatedAt().getTime());
        assertNull(retrieved.get().getAvatar());
        assertNull(retrieved.get().getBirthDate());
        assertNull(retrieved.get().getSalary());
        assertNull(retrieved.get().getTags());
        assertNull(retrieved.get().getDeletedAt());
    }

    @Test
    public void testInsertAndReturnFirstKey_byteArrayNoError() {
        final Jorm db = new Jorm(ds);
        final Employee e = new Employee();
        e.setName("test");
        final byte[] avatarBytes = {0, 1, 2, 3};
        e.setAvatar(avatarBytes);
        final long pk = db.update(Employee.class).omit("pk").obj(e).execInsertAndReturnFirstKey();
        assertTrue(pk > 0);
        final Optional<Employee> retrieved = db.query(Employee.class).where("pk=?", pk).first();
        assertTrue(retrieved.isPresent());
        assertEquals(e.getName(), retrieved.get().getName());
        assertNotNull(retrieved.get().getAvatar());
        assertArrayEquals(avatarBytes, retrieved.get().getAvatar());
    }

    @Test
    public void testQueryFirst() {
        final Jorm db = new Jorm(ds);
        final Optional<Employee> employee = db.query(Employee.class).where("name=?", "Jack").first();
        assertTrue(employee.isPresent());
        final Employee jack = employee.get();
        assertEquals("Jack", jack.getName());
        assertEquals(Gender.MALE, jack.getGender());
        assertEquals(AcademicDegree.NON, jack.getAcademicDegree());
        assertEquals(new BigDecimal("20000.00"), jack.getSalary());
        assertEquals(toDate("1988-12-31"), jack.getBirthDate());
        assertArrayEquals(new String[]{"dev", "t1"}, jack.getTags());
        assertArrayEquals(new String[]{"java", "python"}, jack.getLanguages().toArray());
        assertNotNull(jack.getAttributes());
        assertEquals(2, jack.getAttributes().size());
        assertEquals("value1", jack.getAttributes().get("key1"));
        assertEquals("value2", jack.getAttributes().get("key2"));
        assertFalse(jack.getDuringInternship());
        assertNotNull(jack.getProfile());
        assertEquals("Jack Trump", jack.getProfile().getFullName());
        assertEquals("jack@rocket.com", jack.getProfile().getEmail());
        assertEquals("I am Jack", jack.getProfile().getBio());
        assertNull(jack.getDeletedAt());
        assertEquals(toDateTime("2022-04-07 15:03:45"), jack.getCreatedAt());
        assertEquals(toDateTime("2022-04-07 15:03:45"), jack.getUpdatedAt());
    }

    @Test
    public void testQueryFirst_withChineseChar() {
        final Jorm db = new Jorm(ds);
        final Optional<Employee> employee = db.query(Employee.class).where("name=?", "赵今麦").first();
        assertTrue(employee.isPresent());
        final Employee zjm = employee.get();
        assertEquals("赵今麦", zjm.getName());
        assertEquals(Gender.FEMALE, zjm.getGender());
        assertEquals(AcademicDegree.BACHELOR, zjm.getAcademicDegree());
        assertEquals(new BigDecimal("1000.90"), zjm.getSalary());
        assertEquals(toDate("2008-01-01"), zjm.getBirthDate());
        assertArrayEquals(new String[]{"student"}, zjm.getTags());
        assertNull(zjm.getLanguages());
        assertNull(zjm.getAttributes());
        assertTrue(zjm.getDuringInternship());
        assertNull(zjm.getProfile());
        assertNull(zjm.getDeletedAt());
        assertEquals(toDateTime("2022-04-07 15:03:45"), zjm.getCreatedAt());
        assertEquals(toDateTime("2022-04-07 15:03:45"), zjm.getUpdatedAt());
    }

    @Test
    public void testQueryFind() {
        final Jorm db = new Jorm(ds);
        final List<Employee> list = db.query(Employee.class).find();
        assertNotNull(list);
        assertEquals(4, list.size());
    }

    @Test
    public void testQueryFind_zeroResultButNotNull() {
        final Jorm db = new Jorm(ds);
        final List<Employee> list = db.query(Employee.class).where("name=?", "never").find();
        assertNotNull(list);
        assertEquals(0, list.size());
    }

    @Test
    public void testUpdate() {
        final Jorm db = new Jorm(ds);
        final long affected = db.update(Employee.class)
                .set("academic_degree", AcademicDegree.MASTER)
                .where("name=?", "赵今麦")
                .execUpdate();
        assertEquals(1, affected);
        final Optional<Employee> retrieved = db.query(Employee.class).where("name=?", "赵今麦").first();
        assertTrue(retrieved.isPresent());
        assertEquals(AcademicDegree.MASTER, retrieved.get().getAcademicDegree());
        // 更新时间小于1秒
        assertTrue(System.currentTimeMillis() - retrieved.get().getUpdatedAt().getTime() < 1000);
    }

    @Test
    public void testUpdate_object_nothingChanged() {
        final Jorm db = new Jorm(ds);
        final Optional<Employee> jackOptional = db.query(Employee.class).where("name=?", "Jack").first();
        assertTrue(jackOptional.isPresent());
        final Employee jack = jackOptional.get();
        jack.setUpdatedAt(null); // 设置为 null 以触发自动更新 updated_at 列
//        jack.setCreatedAt(null); // 设置为 null 以触发自动更新 created_at 列
        final long affected = db.update(Employee.class).obj(jack).where("pk=?", jack.getPk()).execUpdate();
        assertEquals(1, affected);

        final Optional<Employee> jackOptional2 = db.query(Employee.class).where("pk=?", jack.getPk()).first();
        assertTrue(jackOptional2.isPresent());
        final Employee jack2 = jackOptional2.get();
        assertEmployeeEquals(jack, jack2);
        assertNull(jack2.getDeletedAt());
        // 更新时间小于1秒
        assertTrue(System.currentTimeMillis() - jack2.getUpdatedAt().getTime() < 1000);
    }

    @Test
    public void testUpdate_object_everythingChanged() {
        final Jorm db = new Jorm(ds);
        final Optional<Employee> jackOptional = db.query(Employee.class).where("name=?", "Jack").first();
        assertTrue(jackOptional.isPresent());
        final Employee jack = jackOptional.get();
        jack.setUpdatedAt(null); // 设置为 null 以触发自动更新 updated_at 列
        jack.setCreatedAt(null); // 设置为 null 以触发自动更新 created_at 列
        jack.setName("New Jack");
        jack.setGender(Gender.FEMALE);
        jack.setAvatar(new byte[]{1, 2, 3, 4});
        jack.setBirthDate(toDate("2010-03-16"));
        jack.setTags(new String[]{"new_tag", "hello"});
        jack.setLanguages(Lists.newArrayList("golang", "swift"));
        final Map<String, Object> attributes = Maps.newHashMap();
        attributes.put("hi", "world");
        jack.setAttributes(attributes);
        jack.setDuringInternship(true);
        jack.setProfile(new Profile("Jack de Trump","jack_de_trump@rocket.com", "Hello World!"));
        final long affected = db.update(Employee.class)
                .obj(jack)
                .where("pk=?", jack.getPk())
                .execUpdate();
        assertEquals(1, affected);
        final Optional<Employee> jackOptional2 = db.query(Employee.class).where("pk=?", jack.getPk()).first();
        assertTrue(jackOptional2.isPresent());
        final Employee jack2 = jackOptional2.get();
        assertEmployeeEquals(jack, jack2);
        assertNull(jack2.getDeletedAt());
        // 更新时间小于1秒
        assertTrue(System.currentTimeMillis() - jack2.getUpdatedAt().getTime() < 1000);
    }

    private void assertEmployeeEquals(Employee expected, Employee given) {
        assertEquals(expected.getName(), given.getName());
        assertEquals(expected.getAcademicDegree(), given.getAcademicDegree());
        assertEquals(expected.getSalary(), given.getSalary());
        assertEquals(expected.getBirthDate(), given.getBirthDate());
        assertArrayEquals(expected.getTags(), given.getTags());
        assertArrayEquals(expected.getLanguages().toArray(), given.getLanguages().toArray());
        assertArrayEquals(expected.getAttributes().keySet().toArray(), given.getAttributes().keySet().toArray());
        assertEquals(expected.getDuringInternship(), given.getDuringInternship());
        assertEquals(expected.getProfile(), given.getProfile());
        assertEquals(expected.getCreatedAt(), given.getCreatedAt());
        assertNull(given.getDeletedAt());
    }
}
