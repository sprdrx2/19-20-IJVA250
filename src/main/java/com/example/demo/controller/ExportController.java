package com.example.demo.controller;

import com.example.demo.entity.Article;
import com.example.demo.entity.Client;
import com.example.demo.entity.Facture;
import com.example.demo.entity.LigneFacture;
import com.example.demo.service.*;
import org.apache.poi.ss.usermodel.*;
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

    @Autowired
    private ExportService exportService;

    @Autowired
    private ArticleService articleService;

    @GetMapping("/clients/csv")
    public void clientsCSV(HttpServletRequest request, HttpServletResponse response) throws IOException {
        response.setContentType("text/csv");
        response.setHeader("Content-Disposition", "attachment; filename=\"clients.csv\"");
        PrintWriter writer = response.getWriter();
        List<Client> allClients = clientService.findAllClients();
        exportService.exportClientsCSV(writer, allClients);
    }

    @GetMapping("/clients/xlsx")
    public void clientsXLSX(HttpServletRequest request, HttpServletResponse response) throws IOException {
        response.setContentType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");
        response.setHeader("ContentDisposition", "attachment; filename=\"clients.xlsx\"");
        Workbook workbook = new XSSFWorkbook();
        List<Client> clientList = clientService.findAllClients();
        exportService.exportCLientsXLSX(workbook, clientList);
        workbook.write(response.getOutputStream());
        workbook.close();
    }

    @GetMapping("/factures/xlsx")
    public void facturesXLSX(HttpServletRequest request, HttpServletResponse response) throws IOException {
        response.setContentType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");
        response.setHeader("ContentDisposition", "attachment; filename=\"factures.xlsx\"");
        Workbook workbook = new XSSFWorkbook();
        List<Facture> factures = factureService.findAllFactures();
        exportService.exportFacturesXLSX(workbook, factures);
        workbook.write(response.getOutputStream());
        workbook.close();
    }

    @GetMapping("/clients/{clientId}/factures/xlsx")
    public void clientFacturesXLSX(@PathVariable Long clientId, HttpServletRequest request, HttpServletResponse response) throws IOException {
        Client client = clientService.getClient(clientId);
        response.setContentType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");
        response.setHeader("ContentDisposition", "attachment; filename=\"client-facture.xlsx\"");
        Workbook workbook = new XSSFWorkbook();
        exportService.exportClientWorkbook(workbook, client);
        workbook.write(response.getOutputStream());
        workbook.close();
    }

    @GetMapping("/factures/details/xlsx")
    public void facturesDetailsXLSX(HttpServletRequest request, HttpServletResponse response) throws IOException {
        response.setContentType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");
        response.setHeader("ContentDisposition", "attachment; filename=\"factures-detaillees.xlsx\"");
        Workbook workbook = new XSSFWorkbook();
        List<Client> clients = clientService.findAllClients();
        exportService.exportAllClientsWorkbook(workbook, clients);
        workbook.write(response.getOutputStream());
        workbook.close();
    }

    @GetMapping("/articles/xlsx")
    public void articlesListXLX(HttpServletRequest request, HttpServletResponse response) throws IOException {
        response.setContentType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");
        response.setHeader("ContentDisposition", "attachment; filename=\"articles.xlsx\"");
        Workbook workbook = new XSSFWorkbook();
        List<Article> articles = articleService.findAllArticles();
        exportService.exportAllArticlesXLSX(workbook, articles);
        workbook.write(response.getOutputStream());
        workbook.close();
    }


}
