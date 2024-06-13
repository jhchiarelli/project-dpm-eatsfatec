package br.gov.sp.fatec.eatsconsumer.models;

import java.io.Serializable;

public class Subscription implements Serializable {
    private String id;
    private String description;
    private String recurrence;
    private Double amount;
    private Boolean active;
    private String idUser;

    public Subscription() {
    }

    public Subscription(String description, String recurrence, Double amount, Boolean active, String idUser) {
        this.description = description;
        this.recurrence = recurrence;
        this.amount = amount;
        this.active = active;
        this.idUser = idUser;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getRecurrence() {
        return recurrence;
    }

    public void setRecurrence(String recurrence) {
        this.recurrence = recurrence;
    }

    public Double getAmount() {
        return amount;
    }

    public void setAmount(Double amount) {
        this.amount = amount;
    }

    public Boolean getActive() {
        return active;
    }

    public void setActive(Boolean active) {
        this.active = active;
    }

    public String getIdUser() {
        return idUser;
    }

    public void setIdUser(String idUser) {
        this.idUser = idUser;
    }
}
