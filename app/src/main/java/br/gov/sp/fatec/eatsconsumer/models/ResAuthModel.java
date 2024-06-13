package br.gov.sp.fatec.eatsconsumer.models;

import com.google.firebase.auth.FirebaseUser;

public class ResAuthModel {
    private FirebaseUser user;
    private String message;

    public ResAuthModel() {
    }

    public ResAuthModel(FirebaseUser user, String message) {
        this.user = user;
        this.message = message;
    }

    public FirebaseUser getUser() {
        return user;
    }

    public void setUser(FirebaseUser user) {
        this.user = user;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}
