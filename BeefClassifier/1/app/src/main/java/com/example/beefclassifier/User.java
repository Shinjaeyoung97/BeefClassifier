package com.example.beefclassifier;

public class User {
    public String email;
    public String name;
    public String uid;

    public User() {
        // Default constructor required for calls to DataSnapshot.getValue(User.class)
    }

    public User(String name, String email, String uid) {
        this.email = email;
        this.name = name;
        this.uid = uid;
    }
}
