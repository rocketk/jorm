package io.github.rocketk.data;

import io.github.rocketk.jorm.anno.JormColumn;
import io.github.rocketk.jorm.anno.JormIgnore;
import io.github.rocketk.jorm.anno.JormTable;

import java.math.BigDecimal;
import java.util.*;

/**
 * @author pengyu
 *
 */
@JormTable(name = "employee", autoGenerateCreatedAt = true, autoGenerateUpdatedAt = true, enableSoftDelete = false)
public class Employee2 {
    private long pk;
    private String name;
    private Gender gender;
    private AcademicDegree academicDegree;
    private BigDecimal salary;
    private String[] tags;
    private List<String> languages;
    private Map<String, Object> attributes;
    @JormColumn(name = "during_internship")
    private Boolean internship;
    private byte[] avatar;
    private Date birthDate;
    private Profile profile;
    private Date createdAt;
    private Date updatedAt;
    private Date deletedAt;
    @JormIgnore
    private String secret;

    public Employee2() {
    }

    public long getPk() {
        return pk;
    }

    public void setPk(long pk) {
        this.pk = pk;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public BigDecimal getSalary() {
        return salary;
    }

    public void setSalary(BigDecimal salary) {
        this.salary = salary;
    }

    public String[] getTags() {
        return tags;
    }

    public void setTags(String[] tags) {
        this.tags = tags;
    }

    public Map<String, Object> getAttributes() {
        return attributes;
    }

    public void setAttributes(Map<String, Object> attributes) {
        this.attributes = attributes;
    }

    public Date getBirthDate() {
        return birthDate;
    }

    public void setBirthDate(Date birthDate) {
        this.birthDate = birthDate;
    }

    public Date getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Date createdAt) {
        this.createdAt = createdAt;
    }

    public Date getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(Date updatedAt) {
        this.updatedAt = updatedAt;
    }

    public Date getDeletedAt() {
        return deletedAt;
    }

    public void setDeletedAt(Date deletedAt) {
        this.deletedAt = deletedAt;
    }

    public AcademicDegree getAcademicDegree() {
        return academicDegree;
    }

    public void setAcademicDegree(AcademicDegree academicDegree) {
        this.academicDegree = academicDegree;
    }

    public String getSecret() {
        return secret;
    }

    public void setSecret(String secret) {
        this.secret = secret;
    }

    public Gender getGender() {
        return gender;
    }

    public void setGender(Gender gender) {
        this.gender = gender;
    }

    public byte[] getAvatar() {
        return avatar;
    }

    public void setAvatar(byte[] avatar) {
        this.avatar = avatar;
    }

    public Profile getProfile() {
        return profile;
    }

    public void setProfile(Profile profile) {
        this.profile = profile;
    }

    public Boolean getInternship() {
        return internship;
    }

    public void setInternship(Boolean internship) {
        this.internship = internship;
    }

    public List<String> getLanguages() {
        return languages;
    }

    public void setLanguages(List<String> languages) {
        this.languages = languages;
    }

    @Override
    public String toString() {
        return new StringJoiner(", ", Employee2.class.getSimpleName() + "[", "]")
                .add("pk=" + pk)
                .add("name='" + name + "'")
                .add("gender=" + gender)
                .add("academicDegree=" + academicDegree)
                .add("salary=" + salary)
                .add("tags=" + Arrays.toString(tags))
                .add("languages=" + languages)
                .add("attributes=" + attributes)
                .add("internship=" + internship)
                .add("avatar=" + Arrays.toString(avatar))
                .add("birthDate=" + birthDate)
                .add("profile=" + profile)
                .add("createdAt=" + createdAt)
                .add("updatedAt=" + updatedAt)
                .add("deletedAt=" + deletedAt)
                .add("secret='" + secret + "'")
                .toString();
    }

}
