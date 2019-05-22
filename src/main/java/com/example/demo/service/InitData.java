package com.example.demo.service;

import com.example.demo.entity.Article;
import com.example.demo.entity.Client;
import com.example.demo.entity.Facture;
import com.example.demo.entity.LigneFacture;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.ApplicationListener;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;
import java.time.LocalDate;
import java.util.Dictionary;
import java.util.List;
import java.util.Random;

/**
 * Classe permettant d'insérer des données dans l'application.
 */
@Service
@Transactional
public class InitData implements ApplicationListener<ApplicationReadyEvent> {

    private Random rand = new Random();

    @Autowired
    private EntityManager em;

    @Override
    public void onApplicationEvent(ApplicationReadyEvent applicationReadyEvent) {
        insertTestData();
    }

    private void insertTestData() {
        Client client1 = newClient("PETRILLO", "Alexandre", LocalDate.of(1983, 1, 3));
        em.persist(client1);

        Client client2 = newClient("Dupont", "Jérome", LocalDate.of(1990, 1, 22));
        em.persist(client2);

        Client client3 = newClient("D\"oe", "Jo;hn", LocalDate.of(2000, 1, 3));
        em.persist(client3);

       Article a1 = new Article();
        a1.setLibelle("Balayette");
        a1.setPrix(3.99);
        em.persist(a1);

        Article a2 = new Article();
        a2.setLibelle("Style espion");
        a2.setPrix(130.0);
        em.persist(a2);

        Article a3 = new Article();
        a3.setLibelle("Vent");
        a3.setPrix(10000.0);
        em.persist(a3);

        Facture f1 = new Facture();
        f1.setClient(client1);
        em.persist(f1);
        em.persist(newLigneFacture(f1, a1, 2));
        em.persist(newLigneFacture(f1, a2, 1));

        Facture f2 = new Facture();
        f2.setClient(client2);
        em.persist(f2);
        em.persist(newLigneFacture(f2, a1, 10));

        Facture f3 = new Facture();
        f3.setClient(client3);
        em.persist(f3);
        em.persist(newLigneFacture(f3, a2, 4));
        em.persist(newLigneFacture(f3, a1, 2));
        em.persist(newLigneFacture(f3, a3, 99));

        Facture f4 = new Facture();
        f4.setClient(client1);
        em.persist(f4);
        em.persist(newLigneFacture(f4,a3, 1));

        String[] newArticles = "AK47,Volvic,Chauffe-eau,Litière,Tapis,Thinkpad T400,Pizza 4 Fromages,Herbe à chien,Paracetamol,Papier Toilette".split(",");
        for (String articleStr : newArticles) {
            Article newArticle = new Article();
            newArticle.setLibelle(articleStr);
            newArticle.setPrix(Math.floor(rand.nextDouble() * 100.0) + .99);
            em.persist(newArticle);
        }

        String[] newClients = "Norimaki Arale,Norimaki Senbei,Norimaki Gatchan,Son Goku,Son Gohan,Son Goten,John Lennon,Paul McMcartney,George Harrison,Ringo Starr".split(",");
        for (String clientStr : newClients) {
            String nom       = clientStr.split(" ")[0];
            String prenom    = clientStr.split(" ")[1];
            Client newClient = new Client();
            newClient.setNom(nom);
            newClient.setPrenom(prenom);
            newClient.setDateNaissance(LocalDate.now());
            em.persist(newClient);
        }
    }


    private LigneFacture newLigneFacture(Facture f, Article a1, int quantite) {
            LigneFacture lf1 = new LigneFacture();
            lf1.setArticle(a1);
            lf1.setQuantite(quantite);
            lf1.setFacture(f);
            return lf1;
    }

    private Client newClient(String nom, String prenom, LocalDate dateNaissance) {
        Client client = new Client();
        client.setNom(nom);
        client.setPrenom(prenom);
        client.setDateNaissance(dateNaissance);
        return client;
    }

}
