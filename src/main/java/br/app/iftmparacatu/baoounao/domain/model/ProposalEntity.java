package br.app.iftmparacatu.baoounao.domain.model;

import jakarta.persistence.*;

import java.sql.Blob;
import java.time.LocalDateTime;

@Entity
public class ProposalEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(length = 100)
    private String title;
    @Column
    private String description;
    @Column
    private Integer likes;
    @Column(length = 100)
    private String situation;
    @Column
    private Boolean active;
    @Column
    private LocalDateTime createdAt;
    @Column(length = 100)
    private String url;
    @Column
    private  Blob photograpy;

    //Usuario usuario; Ciclo ciclo;

    public ProposalEntity(Long id, String title, String description, Integer likes, String situation, Boolean active, LocalDateTime createdAt, String url, Blob photograpy) {
        this.id = id;
        this.title = title;
        this.description = description;
        this.likes = likes;
        this.situation = situation;
        this.active = active;
        this.createdAt = createdAt;
        this.url = url;
        this.photograpy = photograpy;
    }

    public ProposalEntity() {

    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Integer getLikes() {
        return likes;
    }

    public void setLikes(Integer likes) {
        this.likes = likes;
    }

    public String getSituation() {
        return situation;
    }

    public void setSituation(String situation) {
        this.situation = situation;
    }

    public Boolean getActive() {
        return active;
    }

    public void setActive(Boolean active) {
        this.active = active;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public Blob getPhotograpy() {
        return photograpy;
    }

    public void setPhotograpy(Blob photograpy) {
        this.photograpy = photograpy;
    }

}
