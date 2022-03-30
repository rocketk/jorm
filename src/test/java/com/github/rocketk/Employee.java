package com.github.rocketk;

import com.github.rocketk.jorm.anno.JormIgnore;
import com.github.rocketk.jorm.anno.JormTable;
import com.google.common.base.MoreObjects;

import java.math.BigDecimal;
import java.util.Date;
import java.util.Map;

/**
 * @author pengyu
 * @date 2021/12/12
 */
@JormTable(name = "employee")
public class Employee {
    private Long pk;
    private String name;
    private Gender gender;
    private AcademicDegree academicDegree;
    private BigDecimal salary;
    private String[] tags;
    private Map<String, Object> attributes;
    private Boolean duringInternship;
    private byte[] avatar;
    private Date birthDate;
    private Profile profile;
    private ProfileWithoutAnnotation profileWithoutAnnotation;
    private Date createdAt;
    private Date updatedAt;
    private Date deletedAt;
    @JormIgnore
    private String secret;

    public Employee() {
    }

    public Long getPk() {
        return pk;
    }

    public void setPk(Long pk) {
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

    public Boolean getDuringInternship() {
        return duringInternship;
    }

    public void setDuringInternship(Boolean duringInternship) {
        this.duringInternship = duringInternship;
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

    public ProfileWithoutAnnotation getProfileWithoutAnnotation() {
        return profileWithoutAnnotation;
    }

    public void setProfileWithoutAnnotation(ProfileWithoutAnnotation profileWithoutAnnotation) {
        this.profileWithoutAnnotation = profileWithoutAnnotation;
    }

    @Override
    public String toString() {
        return MoreObjects.toStringHelper(this)
                .add("pk", pk)
                .add("name", name)
                .add("gender", gender)
                .add("academicDegree", academicDegree)
                .add("salary", salary)
                .add("tags", tags)
                .add("attributes", attributes)
                .add("duringInternship", duringInternship)
                .add("avatar", avatar)
                .add("birthDate", birthDate)
                .add("profile", profile)
                .add("profileWithoutAnnotation", profileWithoutAnnotation)
                .add("createdAt", createdAt)
                .add("updatedAt", updatedAt)
                .add("deletedAt", deletedAt)
                .add("secret", secret)
                .toString();
    }
}
