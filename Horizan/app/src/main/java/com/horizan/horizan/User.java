package com.horizan.horizan;

public class User {

    private String firstName;
    private String lastName;
    private String email;

    public User(String fN, String lN, String e) {
        firstName = fN;
        lastName = lN;
        email = e;
    }

    public String getFirstName() {
        return firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public String getEmail() {
        return email;
    }

    public void setFirstName(String f) {
        firstName = f;
    }

    public void setLastName(String l) {
        lastName = l;
    }

    public void setEmail(String e) {
        email = e;
    }
}
