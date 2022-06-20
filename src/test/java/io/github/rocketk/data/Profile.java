package io.github.rocketk.data;

import io.github.rocketk.jorm.anno.JormJsonObject;
import com.google.common.base.MoreObjects;

import java.util.Objects;
import java.util.StringJoiner;

/**
 * @author pengyu
 *
 */
@JormJsonObject
public class Profile {
    private String fullName;
    private String email;
    private String bio;

    public Profile() {
    }

    public Profile(String fullName, String email, String bio) {
        this.fullName = fullName;
        this.email = email;
        this.bio = bio;
    }

    public String getFullName() {
        return fullName;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getBio() {
        return bio;
    }

    public void setBio(String bio) {
        this.bio = bio;
    }

    @Override
    public String toString() {
        return new StringJoiner(", ", Profile.class.getSimpleName() + "[", "]")
                .add("fullName='" + fullName + "'")
                .add("email='" + email + "'")
                .add("bio='" + bio + "'")
                .toString();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Profile profile = (Profile) o;
        return Objects.equals(fullName, profile.fullName) && Objects.equals(email, profile.email) && Objects.equals(bio, profile.bio);
    }

    @Override
    public int hashCode() {
        return Objects.hash(fullName, email, bio);
    }
}
