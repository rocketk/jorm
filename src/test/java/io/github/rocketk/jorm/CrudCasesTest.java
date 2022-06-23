package io.github.rocketk.jorm;

import com.alibaba.druid.pool.DruidDataSource;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import io.github.rocketk.data.*;
import io.github.rocketk.jorm.err.WhereClauseAbsentException;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.math.BigDecimal;
import java.sql.SQLException;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import static io.github.rocketk.jorm.util.DateUtil.toDate;
import static io.github.rocketk.jorm.util.DateUtil.toDateTime;
import static org.junit.jupiter.api.Assertions.*;


/**
 * @author pengyu
 */
public abstract class CrudCasesTest {
    protected DruidDataSource ds;

    @BeforeEach
    public void initDataSource() throws SQLException, IOException {
//        ds = BaseDataTest.createDataSourceForHsqlDb();
        initDataSourceInternally();
    }

    @AfterEach
    public void closeDataSource() {
        if (ds != null) {
            ds.close();
        }
    }

    protected abstract void initDataSourceInternally() throws SQLException, IOException;

    protected abstract Jorm createJorm();

    @Test
    public void testInsert() {
        final Jorm db = createJorm();
        final Employee e = new Employee();
        e.setName("test");
        assertTrue(db.mutation(Employee.class).omit("pk").obj(e).insert());
        assertTrue(db.mutation(Employee.class).omit("pk")
                .set("name", "test2")
                .set("created_at", new Date())
                .set("updated_at", new Date())
                .insert());
    }

    @Test
    public void testInsertAndReturnFirstKey() {
        final Jorm db = createJorm();
        final Employee e = new Employee();
        e.setName("test");
        final long pk = db.mutation(Employee.class).omit("pk").obj(e).insertAndReturnFirstKey();
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
        final Jorm db = createJorm();
        final Employee e = new Employee();
        e.setName("test");
        final Date now = new Date();
        e.setUpdatedAt(now);
        final long pk = db.mutation(Employee.class).omit("pk").obj(e).set("created_at", now).insertAndReturnFirstKey();
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
        final Jorm db = createJorm();
        final Employee e = new Employee();
        e.setName("test");
        final byte[] avatarBytes = {0, 1, 2, 3};
        e.setAvatar(avatarBytes);
        final long pk = db.mutation(Employee.class).omit("pk").obj(e).insertAndReturnFirstKey();
        assertTrue(pk > 0);
        final Optional<Employee> retrieved = db.query(Employee.class).where("pk=?", pk).first();
        assertTrue(retrieved.isPresent());
        assertEquals(e.getName(), retrieved.get().getName());
        assertNotNull(retrieved.get().getAvatar());
        assertArrayEquals(avatarBytes, retrieved.get().getAvatar());
    }

    @Test
    public void testQueryFirst() {
        final Jorm db = createJorm();
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
    public void testQueryFirst_withOmit() {
        final Jorm db = createJorm();
        final Optional<Employee> employee = db.query(Employee.class).omit("profile").where("name=?", "Jack").first();
        assertTrue(employee.isPresent());
        final Employee jack = employee.get();
        assertNull(jack.getProfile());
    }

    @Test
    public void testQueryFirst_withSelect() {
        final Jorm db = createJorm();
        final Optional<Employee> employee = db.query(Employee.class)
                .select("pk", "name", "gender")
                .where("name=?", "Jack")
                .first();
        assertTrue(employee.isPresent());
        final Employee jack = employee.get();
        assertEquals("Jack", jack.getName());
        assertEquals(1001, jack.getPk());
        assertEquals(Gender.MALE, jack.getGender());
        assertNull(jack.getAcademicDegree());
        assertNull(jack.getSalary());
        assertNull(jack.getBirthDate());
        assertNull(jack.getTags());
        assertNull(jack.getLanguages());
        assertNull(jack.getAttributes());
        assertNull(jack.getAttributes());
        assertNull(jack.getDuringInternship());
        assertNull(jack.getProfile());
        assertNull(jack.getDeletedAt());
        assertNull(jack.getCreatedAt());
        assertNull(jack.getUpdatedAt());
    }

    @Test
    public void testQueryFirst_withChineseChar() {
        final Jorm db = createJorm();
        final Optional<Employee> employee = db.query(Employee.class).where("name=?", "韩梅梅").first();
        assertTrue(employee.isPresent());
        final Employee zjm = employee.get();
        assertEquals(1004, zjm.getPk());
        assertEquals("韩梅梅", zjm.getName());
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
        final Jorm db = createJorm();
        final List<Employee> list = db.query(Employee.class).find();
        assertNotNull(list);
        assertEquals(4, list.size());
    }

    @Test
    public void testQueryFind_shouldFindDeletedRows() {
        final Jorm db = createJorm();
        final List<Employee> list = db.query(Employee.class).shouldFindDeletedRows(true).find();
        assertNotNull(list);
        assertEquals(5, list.size());
    }

    @Test
    public void testQueryFind_withLimitAndOffsetAndOrderBy() {
        final Jorm db = createJorm();
        final List<Employee> list = db.query(Employee.class)
                .limit(2)
                .offset(1)
                .orderBy("pk asc")
                .find();
        assertNotNull(list);
        assertEquals(2, list.size());
        assertEquals(1002, list.get(0).getPk());
        assertEquals(1003, list.get(1).getPk());
    }

    @Test
    public void testQueryFind_zeroResultButNotNull() {
        final Jorm db = createJorm();
        final List<Employee> list = db.query(Employee.class).where("name=?", "never").find();
        assertNotNull(list);
        assertEquals(0, list.size());
    }

    @Test
    public void testQuery_withColumnAnnotation() {
        final Jorm db = createJorm();
        final Optional<Employee2> zhangsan = db.query(Employee2.class).shouldFindDeletedRows(true).where("name=?", "韩梅梅").first();
        assertTrue(zhangsan.isPresent());
        assertNotNull(zhangsan.get().getInternship());
        assertTrue(zhangsan.get().getInternship());
    }

    @Test
    public void testQuery_withSoftDeleteEnabled() {
        final Jorm db = createJorm();
        assertFalse(db.query(Employee.class).where("name=?", "Elizabeth").first().isPresent());
        assertTrue(db.query(Employee.class).where("name=?", "Elizabeth").shouldFindDeletedRows(true).first().isPresent());
        assertTrue(db.query(Employee2.class).where("name=?", "Elizabeth").first().isPresent());
        assertTrue(db.query(Employee2.class).where("name=?", "Elizabeth").shouldFindDeletedRows(false).first().isPresent());
    }

    @Test
    public void testQuery_count_singleValue() {
        final Jorm db = createJorm();
        final long count = db.query(Long.class).table("employee").count();
        final long count1 = db.query(Employee.class).shouldFindDeletedRows(true).count();
        assertEquals(count1, count);
    }

    @Test
    public void testQuery_count_singleValue2() {
        final Jorm db = createJorm();
        final Optional<Long> countBox = db.query(Long.class).table("employee").select("count(*)").first();
        assertTrue(countBox.isPresent());
        final long count1 = db.query(Employee.class).shouldFindDeletedRows(true).count();
        assertEquals(count1, countBox.get());
        assertEquals(count1, (long) db.query(Integer.class).table("employee").select("count(*)").first().get());
        final List<String> names = db.query(String.class).table("employee").select("name").find();
        assertNotNull(names);
        assertEquals(count1, names.size());
        names.forEach(Assertions::assertNotNull);
    }

    @Test
    public void testQuery_withDateCondition() {
        final Jorm db = createJorm();
        final long count = db.query(Employee.class)
                .where("birth_date>=?", toDate("2000-01-01"))
                .shouldFindDeletedRows(true)
                .count();
        assertEquals(2, count);
    }

    @Test
    public void testQuery_multiWhere() {
        final Jorm db = createJorm();
        final long count = db.query(Employee.class)
                .where("gender = ?", Gender.FEMALE)
                .count();
        assertEquals(2, count);
        final long count2 = db.query(Employee.class)
                .where("gender = ?", Gender.FEMALE)
                .and("salary > ?", 1200)
                .count();
        assertEquals(1, count2);
        final long count3 = db.query(Employee.class)
                .where("gender = ?", Gender.FEMALE)
                .and("salary > ? or 1=1", 1200)
                .count();
        assertEquals(2, count3);
    }

    @Test
    public void testRawQuery() {
        final Jorm db = createJorm();
        final Optional<Employee> zhangsan = db.query(Employee.class)
                .rawSql("select * from employee where birth_date >= ?", toDateTime("2000-01-01 00:00:00"))
                .first();
        assertTrue(zhangsan.isPresent());
    }

    @Test
    public void testRawQuery_aggregate_returnMap() {
        final Jorm db = createJorm();
        final List<Map> result = db.queryMap().rawSql("select gender, count(*) from employee group by gender").find();
        assertNotNull(result);
        assertEquals(2, result.size());
        final Optional<Map> male = db.queryMap().rawSql("select gender, count(*) c from employee group by gender having gender = 1").first();
        assertTrue(male.isPresent());
        final Map map = male.get();
        assertEquals(2, map.size());
        assertTrue(map.containsKey("GENDER") || map.containsKey("gender"));
        assertTrue(map.containsKey("C") || map.containsKey("c"));
    }

    public static class GenderCount {
        private Gender gender;
        private int c;

        public Gender getGender() {
            return gender;
        }

        public void setGender(Gender gender) {
            this.gender = gender;
        }

        public int getC() {
            return c;
        }

        public void setC(int c) {
            this.c = c;
        }
    }

    @Test
    public void testRawQuery_aggregate_returnCustomModel() {
        final Jorm db = createJorm();
        final List<GenderCount> genderCounts = db.query(GenderCount.class).rawSql("select gender, count(*) from employee group by gender").find();
        assertNotNull(genderCounts);
        assertEquals(2, genderCounts.size());
        final Optional<GenderCount> genderCountBox = db.query(GenderCount.class).rawSql("select gender, count(*) c from employee group by gender having gender = 1").first();
        assertTrue(genderCountBox.isPresent());
        final GenderCount genderCount = genderCountBox.get();
        assertEquals(Gender.MALE, genderCount.getGender());
        assertEquals(2, genderCount.getC());
    }

    @Test
    public void testUpdate() {
        final Jorm db = createJorm();
        final long affected = db.mutation(Employee.class)
                .set("academic_degree", AcademicDegree.MASTER)
                .where("name=?", "韩梅梅")
                .update();
        assertEquals(1, affected);
        final Optional<Employee> retrieved = db.query(Employee.class).where("name=?", "韩梅梅").first();
        assertTrue(retrieved.isPresent());
        assertEquals(AcademicDegree.MASTER, retrieved.get().getAcademicDegree());
        // 更新时间小于1秒
        assertTrue(System.currentTimeMillis() - retrieved.get().getUpdatedAt().getTime() < 1000);
    }

    @Test
    public void testUpdate_object_nothingChanged() {
        final Jorm db = createJorm();
        final Optional<Employee> jackOptional = db.query(Employee.class).where("name=?", "Jack").first();
        assertTrue(jackOptional.isPresent());
        final Employee jack = jackOptional.get();
        jack.setUpdatedAt(null); // 设置为 null 以触发自动更新 updated_at 列
//        jack.setCreatedAt(null); // 设置为 null 以触发自动更新 created_at 列
        final long affected = db.mutation(Employee.class).obj(jack).where("pk=?", jack.getPk()).update();
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
        final Jorm db = createJorm();
        final Optional<Employee> jackOptional = db.query(Employee.class).where("name=?", "Jack").first();
        assertTrue(jackOptional.isPresent());
        final Employee jack = jackOptional.get();
        final Date createdAt = jack.getCreatedAt();
        final Date updatedAt = jack.getUpdatedAt();
        jack.setUpdatedAt(null); // 设置为 null 以触发自动更新 updated_at 列
        jack.setCreatedAt(null); // 设置为 null 以避免覆盖 created_at 列
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
        jack.setProfile(new Profile("Jack de Trump", "jack_de_trump@rocket.com", "Hello World!"));
        final long affected = db.mutation(Employee.class)
                .obj(jack)
                .where("pk=?", jack.getPk())
                .update();
        assertEquals(1, affected);
        final Optional<Employee> jackOptional2 = db.query(Employee.class).where("pk=?", jack.getPk()).first();
        assertTrue(jackOptional2.isPresent());
        final Employee jack2 = jackOptional2.get();
        assertEmployeeEquals(jack, jack2);
        assertNull(jack2.getDeletedAt());
        assertEquals(createdAt, jack2.getCreatedAt());
        assertNotEquals(updatedAt, jack2.getUpdatedAt());
        assertNotNull(jack2.getUpdatedAt());
        // 更新时间小于1秒
        assertTrue(System.currentTimeMillis() - jack2.getUpdatedAt().getTime() < 1000);
    }

    @Test
    public void testUpdate_warnIfWhereClauseAbsent() {
        final Jorm db = createJorm();
        try {
            db.mutation(Employee.class).set("attributes", null).update();
            fail("a WhereClauseAbsentException should be thrown");
        } catch (WhereClauseAbsentException e) {
            // success
            assertTrue(e.getMessage().contains("where clause is empty"));
        }
        final long affected = db.mutation(Employee.class).set("attributes", null).ignoreNoWhereClauseWarning(true).update();
        assertEquals(4, affected);
    }

    @Test
    public void testUpdate_shouldUpdateDeletedRows() {
        final Jorm db = createJorm();
        final long affected = db.mutation(Employee.class)
                .set("attributes", null)
                .ignoreNoWhereClauseWarning(true)
                .shouldUpdateDeletedRows(true)
                .update();
        assertEquals(5, affected);
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
//        assertEquals(expected.getCreatedAt(), given.getCreatedAt());
        assertNull(given.getDeletedAt());
    }

    @Test
    public void testDelete_softDelete() {
        final Jorm db = createJorm();
        final long affected = db.mutation(Employee.class).where("name=?", "Jack").delete();
        assertEquals(1, affected);
        final Optional<Employee> e = db.query(Employee.class).where("name=?", "Jack").shouldFindDeletedRows(true).first();
        assertTrue(e.isPresent());
        final Employee jack = e.get();
        assertNotNull(jack.getDeletedAt());
        assertTrue(System.currentTimeMillis() - jack.getDeletedAt().getTime() < 1000);
    }

    @Test
    public void testDelete_warningIfWhereClauseAbsent() {
        final Jorm db = createJorm();
        try {
            final long affected = db.mutation(Employee.class).delete();
            fail("a WhereClaauseAbsentException should be thrown");
        } catch (WhereClauseAbsentException e) {
            assertTrue(e.getMessage().contains("where clause is empty"));
        }
    }

    @Test
    public void testDelete_noWhereClauseWarning_disabled() {
        final Jorm db = createJorm();
        final long count = db.query(Employee.class).count();
        final long affected = db.mutation(Employee.class).ignoreNoWhereClauseWarning(true).delete();
        assertEquals(count, affected);
    }

    @Test
    public void testDelete_hardDelete() {
        final Jorm db = createJorm();
        final long affected = db.mutation(Employee2.class).where("name=?", "Jack").delete();
        assertEquals(1, affected);
        final Optional<Employee2> e = db.query(Employee2.class).where("name=?", "Jack").shouldFindDeletedRows(true).first();
        assertFalse(e.isPresent());
    }

    @Test
//    @Disabled
    public void testTransaction() {
        final Jorm db = createJorm();
        final BigDecimal jackNewSalary = new BigDecimal("654321.00");
        final BigDecimal benjaminNewSalary = new BigDecimal("123456.00");
        final boolean success = db.transaction().operations(t -> {
            t.mutation(Employee.class).where("name=?", "Jack").set("salary", jackNewSalary).update();
            t.mutation(Employee.class).where("name=?", "Benjamin").set("salary", benjaminNewSalary).update();
        }).commit();
        assertTrue(success);
        db.query(Employee.class).where("name=?", "Jack").first().ifPresent(e -> assertEquals(jackNewSalary, e.getSalary()));
        db.query(Employee.class).where("name=?", "Benjamin").first().ifPresent(e -> assertEquals(benjaminNewSalary, e.getSalary()));
    }

    @Test
//    @Disabled
    public void testTransaction_fail() {
        final Jorm db = createJorm();
        final BigDecimal jackNewSalary = new BigDecimal("654321.00");
        final BigDecimal benjaminNewSalary = new BigDecimal("123456.00");
        final boolean success = db.transaction().operations(t -> {
            t.mutation(Employee.class).where("name=?", "Jack").set("salary", jackNewSalary).update();
            t.mutation(Employee.class).where("name=?", "Benjamin").set("salary1", benjaminNewSalary).update();
        }).onError((t, e) -> {
            System.out.println("operation failed, caused by: " + e.getMessage());
            t.rollback();
        }).commit();
        assertFalse(success);
        db.query(Employee.class).where("name=?", "Jack").first().ifPresent(e -> assertNotEquals(jackNewSalary, e.getSalary()));
        db.query(Employee.class).where("name=?", "Benjamin").first().ifPresent(e -> assertNotEquals(benjaminNewSalary, e.getSalary()));
    }
}
