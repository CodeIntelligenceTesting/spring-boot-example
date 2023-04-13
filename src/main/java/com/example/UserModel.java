package com.example;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.Serializable;

public class UserModel implements Serializable {

    private String name;
    private static final long serialVersionUID = 123456789L;

    public UserModel(String name) {
        this.name = name;
    }

    private void readObject(ObjectInputStream ois) throws IOException, ClassNotFoundException {
        name = (String) ois.readObject();
    }
}
