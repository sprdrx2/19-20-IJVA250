package com.example.demo.entity;

import javax.persistence.*;
import java.util.Set;

@Entity
public class Facture {
    @Id
    @GeneratedValue
    private Long id;

    @ManyToOne
    private Client client;

    @OneToMany(mappedBy = "facture")
    private Set<LigneFacture> ligneFactures;

    public Long getId() {
        return id;
    }

    public Client getClient() {
        return client;
    }

    public Set<LigneFacture> getLigneFactures() {
        return ligneFactures;
    }

    public void setClient(Client client) {
        this.client = client;
    }

    public void setLigneFactures(Set<LigneFacture> ligneFactures) {
        this.ligneFactures = ligneFactures;
    }

    public void setId(Long id) {
        this.id = id;
    }
}
