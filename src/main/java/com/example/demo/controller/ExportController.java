package com.example.demo.controller;

import com.example.demo.entity.Client;
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
        Sheet sheet = workbook.createSheet("Factures");
        Integer rowIndex = 0;
        for (Client client : clientService.findAllClients()) {
            Row headerRow = sheet.createRow(rowIndex);
            Cell cellId = headerRow.createCell(0);
            Cell cellNom = headerRow.createCell(1);
            Cell cellPrenom = headerRow.createCell(2);
            Cell cellDateNaissance = headerRow.createCell(3);
            Cell cellAge = headerRow.createCell(4);
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
        Sheet sheet = workbook.createSheet("Clients");

        workbook.write(response.getOutputStream());
    }

}
