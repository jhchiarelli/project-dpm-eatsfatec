package br.gov.sp.fatec.eatsconsumer.model;

import java.io.Serializable;

public class Consumer implements Serializable {
    private String id;
    private String name;
    private String email;
    private String phone;
    private String address;
    private String level;
    private String urlImage;
    private Boolean active;
    private String idUser;

    public Consumer() {
    }

    public Consumer(String name, String phone, String address, String urlImage, Boolean active) {
        this.name = name;
        this.phone = phone;
        this.address = address;
        this.urlImage = urlImage;
        this.active = active;
    }

    public Consumer(String name, String email, String phone, String address, String level, String urlImage, Boolean active, String idUser) {
        this.name = name;
        this.email = email;
        this.phone = phone;
        this.address = address;
        this.level = level;
        this.urlImage = urlImage;
        this.active = active;
        this.idUser = idUser;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getLevel() {
        return level;
    }

    public void setLevel(String level) {
        this.level = level;
    }

    public String getUrlImage() {
        return urlImage;
    }

    public void setUrlImage(String urlImage) {
        this.urlImage = urlImage;
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
