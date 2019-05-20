package com.example.demo.entity;

import net.bytebuddy.asm.Advice;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.ManyToOne;

@Entity
public class LigneFacture {

    @Id
    @GeneratedValue
    private Long Id;

    @ManyToOne
    private Facture facture;

    @ManyToOne
    private Article article;

}
