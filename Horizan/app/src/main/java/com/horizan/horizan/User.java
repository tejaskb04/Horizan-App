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
}
