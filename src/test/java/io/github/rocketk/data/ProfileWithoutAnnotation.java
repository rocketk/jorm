package io.github.rocketk.data;

import com.google.common.base.MoreObjects;

/**
 * @author pengyu
 *
 */
public class ProfileWithoutAnnotation {
    private String fullName;
    private String email;
    private String bio;

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
        return MoreObjects.toStringHelper(this)
                .add("fullName", fullName)
                .add("email", email)
                .add("bio", bio)
                .toString();
    }
}
