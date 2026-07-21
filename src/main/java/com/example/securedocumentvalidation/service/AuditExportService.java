package com.example.securedocumentvalidation.service;

import com.example.securedocumentvalidation.entity.AuditLog;
import com.example.securedocumentvalidation.repository.AuditLogRepository;

import com.itextpdf.text.Document;
import com.itextpdf.text.Paragraph;
import com.itextpdf.text.pdf.PdfWriter;

import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import org.springframework.stereotype.Service;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.util.List;

@Service
public class AuditExportService {

    private final AuditLogRepository repository;

    public AuditExportService(
            AuditLogRepository repository) {

        this.repository = repository;
    }

    // ==========================
    // EXPORT TO EXCEL
    // ==========================
    public ByteArrayInputStream exportToExcel()
            throws Exception {

        Workbook workbook =
                new XSSFWorkbook();

        Sheet sheet =
                workbook.createSheet("Audit Logs");

        Row header =
                sheet.createRow(0);

        header.createCell(0).setCellValue("ID");
        header.createCell(1).setCellValue("Username");
        header.createCell(2).setCellValue("Action");
        header.createCell(3).setCellValue("Document ID");
        header.createCell(4).setCellValue("Timestamp");

        List<AuditLog> logs =
                repository.findAll();

        int rowIdx = 1;

        for (AuditLog log : logs) {

            Row row =
                    sheet.createRow(rowIdx++);

            row.createCell(0)
                    .setCellValue(log.getId());

            row.createCell(1)
                    .setCellValue(log.getUsername());

            row.createCell(2)
                    .setCellValue(
                            log.getAction().name()
                    );

            row.createCell(3)
                    .setCellValue(
                            log.getDocumentId()
                    );

            row.createCell(4)
                    .setCellValue(
                            log.getTimestamp()
                                    .toString()
                    );
        }

        ByteArrayOutputStream out =
                new ByteArrayOutputStream();

        workbook.write(out);
        workbook.close();

        return new ByteArrayInputStream(
                out.toByteArray()
        );
    }

    // ==========================
    // EXPORT TO PDF
    // ==========================
    public ByteArrayInputStream exportToPdf()
            throws Exception {

        Document document =
                new Document();

        ByteArrayOutputStream out =
                new ByteArrayOutputStream();

        PdfWriter.getInstance(
                document,
                out
        );

        document.open();

        document.add(
                new Paragraph(
                        "Audit Log Report"
                )
        );

        List<AuditLog> logs =
                repository.findAll();

        for (AuditLog log : logs) {

            document.add(
                    new Paragraph(
                            "ID: " + log.getId()
                                    + " | User: "
                                    + log.getUsername()
                                    + " | Action: "
                                    + log.getAction()
                                    + " | Document ID: "
                                    + log.getDocumentId()
                                    + " | Time: "
                                    + log.getTimestamp()
                    )
            );
        }

        document.close();

        return new ByteArrayInputStream(
                out.toByteArray()
        );
    }
}