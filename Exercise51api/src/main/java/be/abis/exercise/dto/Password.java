package be.abis.exercise.dto;

import jakarta.validation.constraints.Size;

public class Password {

    @Size(min=5,message="password should be longer than 5 characters")
    private String password;

    public Password(){}

    public Password(String password) {
        this.password = password;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }
}
