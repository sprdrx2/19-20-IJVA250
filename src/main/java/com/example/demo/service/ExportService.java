package com.example.demo.service;

import com.example.demo.entity.Article;
import com.example.demo.entity.Client;
import com.example.demo.entity.Facture;
import com.example.demo.entity.LigneFacture;
import org.apache.poi.ss.usermodel.*;
import org.hibernate.jdbc.Work;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.print.DocFlavor;
import java.io.PrintWriter;
import java.io.Writer;
import java.util.List;

@Service
public class ExportService {

    @Autowired
    private FactureService factureService;

    public void exportClientsCSV(PrintWriter writer, List<Client> clientsList) {
        writer.println("Id;Nom;Prenom;Date de Naissance;Age");
        for (Client client : clientsList) {
            writer.println(client.toCSV());
        }
    }

    public void exportCLientsXLSX(Workbook workbook, List<Client> clientsList) {
        Sheet sheet = workbook.createSheet("Clients");
        String[] headers = {"ID", "NOM", "PRENOM", "DATE DE NAISSANCE", "AGE"};
        makeHeaderRow(sheet, headers);

        Integer rowIndex = 1;
        for (Client client : clientsList) {
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
    }

    public void exportFacturesXLSX(Workbook workbook, List<Facture> facturesList) {
        Sheet sheet = workbook.createSheet("Factures");
        String[] headers = {"ID", "CLIENT Nom", "CLIENT Prénom", "NOMBRE ARTICLES", "TOTAL"};
        makeHeaderRow(sheet, headers);

        Integer rowIndex = 1;
        for (Facture facture : facturesList ) {
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
    }

    public void exportClientWorkbook(Workbook workbook, Client client) {
        makeClientWorkbook(workbook, client);
    }

    public void exportAllClientsWorkbook(Workbook workbook, List<Client> clientsList) {
        for (Client client : clientsList) {
            makeClientWorkbook(workbook, client);
        }
    }

    private void makeClientWorkbook(Workbook workbook, Client client) {
        String patronymeClient = client.getNom() + " " + client.getPrenom();
        Sheet clientSheet = workbook.createSheet(patronymeClient);
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
            Sheet factureSheet = workbook.createSheet(patronymeClient + " - Facture " + facture.getId().toString());
            String[] headers = {"ARTICLE ID", "LIBELLE", "QUANTITE","PRIX UNITAIRE","SOUS-TOTAL"};
            makeHeaderRow(factureSheet, headers);

            Integer rowIndex = 1;
            for (LigneFacture ligneFacture : facture.getLigneFactures()) {
                Row ligneFactureRow = factureSheet.createRow(rowIndex);
                Cell cellArticleId = ligneFactureRow.createCell(0);
                Cell cellArticleLib = ligneFactureRow.createCell(1);
                Cell cellQuantite = ligneFactureRow.createCell(2);
                Cell cellPrixUnit = ligneFactureRow.createCell(3);
                Cell cellSubTotal = ligneFactureRow.createCell(4);

                cellArticleId.setCellValue(ligneFacture.getArticle().getId());
                cellArticleLib.setCellValue(ligneFacture.getArticle().getLibelle());
                cellQuantite.setCellValue(ligneFacture.getQuantite());
                cellPrixUnit.setCellValue(ligneFacture.getArticle().getPrix());
                cellSubTotal.setCellValue(ligneFacture.getSousTotal());

                rowIndex++;
            }
            Row ligneTotal = factureSheet.createRow(rowIndex);
            CellStyle cellStyle = workbook.createCellStyle();
            Font font = workbook.createFont();
            font.setColor(IndexedColors.RED.getIndex());
            font.setBold(true);
            cellStyle.setFont(font);
            Cell cell1 = ligneTotal.createCell(0);
            Cell cell2 = ligneTotal.createCell(1);
            cell1.setCellStyle(cellStyle);
            cell2.setCellStyle(cellStyle);
            cell1.setCellValue("TOTAL :");
            cell2.setCellValue(facture.calculateTotal());
        }
    }

    public void exportAllArticlesXLSX(Workbook workbook, List<Article> articles) {
        Sheet sheet = workbook.createSheet("Articles");
        String[] headers = {"Libellé", "Prix"};
        makeHeaderRow(sheet, headers);

        Integer rowIndex = 1;
        for (Article article : articles) {
            Row row = sheet.createRow(rowIndex);
            row.createCell(0).setCellValue(article.getLibelle());
            row.createCell(1).setCellValue(article.getPrix());
            rowIndex++;
        }
    }

    private void makeHeaderRow(Sheet sheet, String[] headers) {
        CellStyle headerStyle = makeHeaderStyle(sheet.getWorkbook());
        Row headerRow = sheet.createRow(0);
        Integer cellIndex = 0;
        for (String header : headers) {
            Cell hdrCell = headerRow.createCell(cellIndex);
            hdrCell.setCellStyle(headerStyle);
            hdrCell.setCellValue(header);
            sheet.setColumnWidth(cellIndex,20 * 256);
            cellIndex++;
        }
    }

    private CellStyle makeHeaderStyle(Workbook workbook) {
        CellStyle headerStyle = workbook.createCellStyle();
        Font font = workbook.createFont();
        font.setBold(true);
        font.setColor(IndexedColors.WHITE.getIndex());
        headerStyle.setFont(font);
        headerStyle.setFillForegroundColor(IndexedColors.BLACK.getIndex());
        headerStyle.setFillPattern(FillPatternType.SOLID_FOREGROUND);
        return headerStyle;
    }


}
