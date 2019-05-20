package com.example.demo.controller;

import com.example.demo.entity.Client;
import com.example.demo.entity.Facture;
import com.example.demo.entity.LigneFacture;
import com.example.demo.service.*;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.persistence.criteria.CriteriaBuilder;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.time.LocalDate;
import java.util.List;

/**
 * Controlleur pour réaliser les exports.
 */
@Controller
@RequestMapping("/")
public class ExportController {

    @Autowired
    private ClientService clientService;

    @Autowired
    private FactureService factureService;

    @GetMapping("/clients/csv")
    public void clientsCSV(HttpServletRequest request, HttpServletResponse response) throws IOException {
        response.setContentType("text/csv");
        response.setHeader("Content-Disposition", "attachment; filename=\"clients.csv\"");
        PrintWriter writer = response.getWriter();
        List<Client> allClients = clientService.findAllClients();
        LocalDate now = LocalDate.now();
        writer.println("Id;Nom;Prenom;Date de Naissance;Age");
        List<Client> clientList = clientService.findAllClients();
        for (Client client : clientList) {
            writer.println(client.toCSV());
        }
    }

    @GetMapping("/clients/xlsx")
    public void clientsXLSX(HttpServletRequest request, HttpServletResponse response) throws IOException {
        response.setContentType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");
        response.setHeader("ContentDisposition", "attachment; filename=\"clients.xlsx\"");
        Workbook workbook = new XSSFWorkbook();
        Sheet sheet = workbook.createSheet("Clients");

        Row headerRow               = sheet.createRow(0);
        Cell hdrCellId              = headerRow.createCell(0);
        Cell hdrCellNom             = headerRow.createCell(1);
        Cell hdrCellPrenom          = headerRow.createCell(2);
        Cell hdrCellDateNaissance   = headerRow.createCell(3);
        Cell hdrCellAge             = headerRow.createCell(4);
        hdrCellId.setCellValue("ID");
        hdrCellNom.setCellValue("NOM");
        hdrCellPrenom.setCellValue("PRENOM");
        hdrCellDateNaissance.setCellValue("DATE DE NAISSANCE");
        hdrCellAge.setCellValue("AGE");

        Integer rowIndex = 1;
        for (Client client : clientService.findAllClients()) {
            Row clientRow           = sheet.createRow(rowIndex);
            Cell cellId             = clientRow.createCell(0);
            Cell cellNom            = clientRow.createCell(1);
            Cell cellPrenom         = clientRow.createCell(2);
            Cell cellDateNaissance  = clientRow.createCell(3);
            Cell cellAge            = clientRow.createCell(4);
            cellId.setCellValue(client.getId());
            cellNom.setCellValue(client.getNom());
            cellPrenom.setCellValue(client.getPrenom());
            cellDateNaissance.setCellValue(client.getDateNaissance().toString());
            cellAge.setCellValue(client.calculateAge());
            rowIndex++;
        }
        workbook.write(response.getOutputStream());
        workbook.close();
    }

    @GetMapping("/factures/xlsx")
    public void facturesXLSX(HttpServletRequest request, HttpServletResponse response) throws IOException {
        response.setContentType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");
        response.setHeader("ContentDisposition", "attachment; filename=\"factures.xlsx\"");
        Workbook workbook = new XSSFWorkbook();
        Sheet sheet = workbook.createSheet("Factures");

        Row headerRow           = sheet.createRow(0);
        Cell hdrCellId          = headerRow.createCell(0);
        Cell hdrCellClientNom   = headerRow.createCell(1);
        Cell hdrCellClientPrenom= headerRow.createCell(2);
        Cell hdrCellNbrArticles = headerRow.createCell(3);
        Cell hdrCellTotal       = headerRow.createCell(4);
        hdrCellId.setCellValue("ID");
        hdrCellClientNom.setCellValue("CLIENT Nom");
        hdrCellClientPrenom.setCellValue("CLIENT Prenom");
        hdrCellNbrArticles.setCellValue("NOMBRE ARTICLES");
        hdrCellTotal.setCellValue("TOTAL");

        Integer rowIndex = 1;
        for (Facture facture : factureService.findAllFactures() ) {
            Row factureRow          = sheet.createRow(rowIndex);
            Cell cellID             = factureRow.createCell(0);
            Cell cellClientNom      = factureRow.createCell(1);
            Cell cellClientPrenom   = factureRow.createCell(2);
            Cell cellNbrArticles    = factureRow.createCell(3);
            Cell cellTotal          = factureRow.createCell(4);
            cellID.setCellValue(facture.getId());
            cellClientNom.setCellValue(facture.getClient().getNom());
            cellClientPrenom.setCellValue(facture.getClient().getPrenom());
            cellNbrArticles.setCellValue(facture.calculateNombreArticles());
            cellTotal.setCellValue(facture.calculateTotal());
            rowIndex++;
        }
        workbook.write(response.getOutputStream());
        workbook.close();
    }

    @GetMapping("/clients/{clientId}/factures/xlsx")
    public void clientFacturesXLSX(@PathVariable Long clientId, HttpServletRequest request, HttpServletResponse response) throws IOException {
        Client client = clientService.getClient(clientId);
        response.setContentType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");
        response.setHeader("ContentDisposition", "attachment; filename=\"client-facture.xlsx\"");
        Workbook workbook = new XSSFWorkbook();

        Sheet clientSheet = workbook.createSheet("Client");
        Row row1 = clientSheet.createRow(0);
        Row row2 = clientSheet.createRow(1);
        Row row3 = clientSheet.createRow(2);
        Row row4 = clientSheet.createRow(3);
        row1.createCell(0).setCellValue("Nom");
        row1.createCell(1).setCellValue(client.getNom());
        row2.createCell(0).setCellValue("Prenom");
        row2.createCell(1).setCellValue(client.getPrenom());
        row3.createCell(0).setCellValue("Date de Naissance");
        row3.createCell(1).setCellValue(client.getDateNaissance().toString());
        row4.createCell(0).setCellValue("Age");
        row4.createCell(1).setCellValue(client.calculateAge());

        //for(Facture facture : client.getFactures()) {
        for (Facture facture : factureService.getClientFactures(client)) {
            Sheet factureSheet = workbook.createSheet("Facture " + facture.getId().toString());
            Row headerRow = factureSheet.createRow(0);
            Cell hdrCellArticleId   = headerRow.createCell(0);
            Cell hdrCellArticleLib  = headerRow.createCell(1);
            Cell hdrCellQuantite    = headerRow.createCell(2);
            Cell hdrCellPrixUnit    = headerRow.createCell(3);
            Cell hdrCellSubTotal    = headerRow.createCell(4);

            hdrCellArticleId.setCellValue("ARTICLE ID");
            hdrCellArticleLib.setCellValue("LIBELE");
            hdrCellQuantite.setCellValue("QUANTITE");
            hdrCellPrixUnit.setCellValue("PRIX UNITAIRE");
            hdrCellSubTotal.setCellValue("SOUS-TOTAL");

            Integer rowIndex = 1;
            for (LigneFacture ligneFacture : facture.getLigneFactures()) {
                Row ligneFactureRow  = factureSheet.createRow(rowIndex);
                Cell cellArticleId   = ligneFactureRow.createCell(0);
                Cell cellArticleLib  = ligneFactureRow.createCell(1);
                Cell cellQuantite    = ligneFactureRow.createCell(2);
                Cell cellPrixUnit    = ligneFactureRow.createCell(3);
                Cell cellSubTotal    = ligneFactureRow.createCell(4);

                cellArticleId.setCellValue(ligneFacture.getArticle().getId());
                cellArticleLib.setCellValue(ligneFacture.getArticle().getLibelle());
                cellQuantite.setCellValue(ligneFacture.getQuantite());
                cellPrixUnit.setCellValue(ligneFacture.getArticle().getPrix());
                cellSubTotal.setCellValue(ligneFacture.getSousTotal());

                rowIndex++;
            }
            Row ligneTotal = factureSheet.createRow(rowIndex);
            ligneTotal.createCell(0).setCellValue("TOTAL :");
            ligneTotal.createCell(1).setCellValue(facture.calculateTotal());
        }
        workbook.write(response.getOutputStream());
        workbook.close();
    }

}
