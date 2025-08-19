package com.example.demo.service;

import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.layout.Document;
import com.itextpdf.layout.element.Paragraph;

import java.io.ByteArrayOutputStream;
import java.io.IOException;

@Service
public class AlternativePdfService {

    public byte[] convertWordToPdfWithPOI(MultipartFile file) throws Exception {
        try {
            System.out.println("Starting simple PDF conversion for file: " + file.getOriginalFilename());
            System.out.println("File size: " + file.getSize() + " bytes");
            
            // Create a simple PDF with basic content
            ByteArrayOutputStream pdfOutputStream = new ByteArrayOutputStream();
            PdfWriter writer = new PdfWriter(pdfOutputStream);
            PdfDocument pdfDoc = new PdfDocument(writer);
            Document pdfDocument = new Document(pdfDoc);
            
            // Add basic content
            pdfDocument.add(new Paragraph("Converted from: " + file.getOriginalFilename()));
            pdfDocument.add(new Paragraph("Original file size: " + file.getSize() + " bytes"));
            pdfDocument.add(new Paragraph("Conversion date: " + new java.util.Date()));
            pdfDocument.add(new Paragraph(""));
            pdfDocument.add(new Paragraph("Note: This is a simplified conversion. The original Word document content"));
            pdfDocument.add(new Paragraph("could not be fully processed due to library limitations."));
            pdfDocument.add(new Paragraph(""));
            pdfDocument.add(new Paragraph("For full Word to PDF conversion, please use a dedicated"));
            pdfDocument.add(new Paragraph("conversion service or software."));
            
            // Close document
            pdfDocument.close();
            
            byte[] pdfBytes = pdfOutputStream.toByteArray();
            System.out.println("Successfully created simple PDF. Size: " + pdfBytes.length + " bytes");
            
            return pdfBytes;
            
        } catch (Exception e) {
            System.err.println("Simple PDF creation failed: " + e.getMessage());
            e.printStackTrace();
            throw new RuntimeException("Simple PDF creation failed: " + e.getMessage(), e);
        }
    }
}
