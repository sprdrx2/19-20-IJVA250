package com.example.demo.controller;

import com.example.demo.entity.Client;
import com.example.demo.entity.Facture;
import com.example.demo.service.*;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

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
    }

}
