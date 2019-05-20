package com.example.demo.entity;

import javax.persistence.*;

@Entity
public class Article {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @Column
    private String libelle;

    @Column
    private Double prix;

    public Long getId() { return this.id; }
    public Double getPrix() { return this.prix; }
    public String getLibelle() { return this.libelle; }

    public void setPrix(Double prix) { this.prix = prix; }
    public void setLibelle(String libelle) { this.libelle = libelle; }


}
