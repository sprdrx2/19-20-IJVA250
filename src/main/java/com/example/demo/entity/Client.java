package com.example.demo.entity;

import javax.persistence.*;
import java.lang.invoke.StringConcatFactory;
import java.time.LocalDate;
import java.util.Set;

/**
 * Created by Alexandre on 09/04/2018.
 */
@Entity
public class Client {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @Column
    private String prenom;

    @Column
    private String nom;

    @Column
    private LocalDate dateNaissance;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getPrenom() {
        return prenom;
    }

    public void setPrenom(String prenom) {
        this.prenom = prenom;
    }

    public String getNom() {
        return nom;
    }

    public void setNom(String nom) {
        this.nom = nom;
    }

    public LocalDate getDateNaissance() {
        return dateNaissance;
    }

    public void setDateNaissance(LocalDate dateNaissance) {
        this.dateNaissance = dateNaissance;
    }

    public Integer calculateAge() {
        return LocalDate.now().getYear() -  this.dateNaissance.getYear();
    }

    public String toCSV() {
        // beurk
        // replaceAll("\\\"","\\\\\"")
        return this.id + ";" + '"' + this.nom + '"' + ";" + '"' + this.prenom + '"' + ";" + this.dateNaissance.toString() + ";" + this.calculateAge().toString();
    }
}
