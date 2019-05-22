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

        for (Client client : clientsList) {
            Object[] rowContent = {client.getId(), client.getNom(), client.getPrenom(), client.getDateNaissance(), client.calculateAge()};
            appendRow(sheet, rowContent);
        }
    }

    public void exportFacturesXLSX(Workbook workbook, List<Facture> facturesList) {
        Sheet sheet = workbook.createSheet("Factures");
        String[] headers = {"ID", "CLIENT Nom", "CLIENT Prénom", "NOMBRE ARTICLES", "TOTAL"};
        makeHeaderRow(sheet, headers);

        Integer rowIndex = 1;
        for (Facture facture : facturesList ) {
            Object[] rowContent = {facture.getId(), facture.getClient().getNom(), facture.getClient().getPrenom(), facture.calculateNombreArticles(), facture.calculateTotal()};
            appendRow(sheet, rowContent);
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
        String clientSheetTitle = patronymeClient + " " + client.getDateNaissance();
        Sheet clientSheet = workbook.createSheet(clientSheetTitle);
        String[] hdrs = {"ID", "NOMBRE ARTICLES", "TOTAL"};
        makeHeaderRow(clientSheet, hdrs);

        for (Facture facture : factureService.getClientFactures(client)) {
            Object[] clientSheetRowContent = {facture.getId(), facture.calculateNombreArticles(), facture.calculateTotal()};
            appendRow(clientSheet, clientSheetRowContent);

            Sheet factureSheet = workbook.createSheet(patronymeClient + " - Facture " + facture.getId().toString());
            String[] headers = {"ARTICLE ID", "LIBELLE", "QUANTITE","PRIX UNITAIRE","SOUS-TOTAL"};
            makeHeaderRow(factureSheet, headers);

            Integer rowIndex = 1;
            for (LigneFacture ligneFacture : facture.getLigneFactures()) {
                Object[] rowContent = {
                        ligneFacture.getArticle().getId(),
                        ligneFacture.getArticle().getLibelle(),
                        ligneFacture.getQuantite(),
                        ligneFacture.getArticle().getPrix(),
                        ligneFacture.getSousTotal()
                };
                appendRow(factureSheet, rowContent);
            }
            Row ligneTotal = factureSheet.createRow(factureSheet.getLastRowNum() + 1);
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

        for (Article article : articles) {
            Object[] rowContent = {article.getLibelle(), article.getLibelle()};
            appendRow(sheet, rowContent);
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

    private void appendRow(Sheet sheet, Object[] objects) {
        Integer lastRow = sheet.getLastRowNum();
        Row newRow = sheet.createRow(lastRow +1);
        Integer cellIndex = 0;
        for (Object object : objects){
            Cell newCell = newRow.createCell(cellIndex);
            newCell.setCellValue(object.toString());
            cellIndex++;
        }
    }

}
