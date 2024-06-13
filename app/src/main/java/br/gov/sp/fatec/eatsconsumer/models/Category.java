package br.gov.sp.fatec.eatsconsumer.models;

import java.io.Serializable;

public class Category implements Serializable {
    private String id;
    private String description;
    private String imageUrl;
    private Boolean active;

    public Category() {
    }

    public Category(String description, String imageUrl, Boolean active) {
        this.description = description;
        this.imageUrl = imageUrl;
        this.active = active;
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

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public Boolean getActive() {
        return active;
    }

    public void setActive(Boolean active) {
        this.active = active;
    }

    @Override
    public String toString() {
        return description;
    }
}