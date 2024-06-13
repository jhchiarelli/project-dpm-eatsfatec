package br.gov.sp.fatec.eatsconsumer.models;

import java.io.Serializable;

public class Publicity implements Serializable {
    private String id;
    private String name;
    private String imageUrl;
    private String targetLink;
    private Boolean active;

    public Publicity() {
    }

    public Publicity(String name, String imageUrl, String targetLink, Boolean active) {
        this.name = name;
        this.imageUrl = imageUrl;
        this.targetLink = targetLink;
        this.active = active;
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

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public String getTargetLink() {
        return targetLink;
    }

    public void setTargetLink(String targetLink) {
        this.targetLink = targetLink;
    }

    public Boolean getActive() {
        return active;
    }

    public void setActive(Boolean active) {
        this.active = active;
    }
}
