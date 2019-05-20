package com.example.demo.entity;

import net.bytebuddy.asm.Advice;

import javax.persistence.*;

@Entity
public class LigneFacture {

    @Id
    @GeneratedValue
    private Long Id;

    @ManyToOne
    private Facture facture;

    @ManyToOne
    private Article article;

    @Column
    private Integer quantite;

    public Long getId() {
        return Id;
    }

    public Article getArticle() {
        return article;
    }

    public Facture getFacture() {
        return facture;
    }

    public Integer getQuantite() {
        return quantite;
    }

    public void setId(Long id) {
        Id = id;
    }

    public void setArticle(Article article) {
        this.article = article;
    }

    public void setFacture(Facture facture) {
        this.facture = facture;
    }

    public void setQuantite(Integer quantite) {
        this.quantite = quantite;
    }
}


