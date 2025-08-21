package com.example.demo.service;

import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.layout.Document;
import com.itextpdf.layout.element.Paragraph;
import com.itextpdf.kernel.font.PdfFont;
import com.itextpdf.kernel.font.PdfFontFactory;
import com.itextpdf.io.font.constants.StandardFonts;
import com.itextpdf.layout.element.Table;
import com.itextpdf.layout.element.Cell;
import com.itextpdf.layout.element.Image;
import com.itextpdf.io.image.ImageDataFactory;
import com.itextpdf.layout.properties.TextAlignment;
import com.itextpdf.layout.properties.UnitValue;
import com.itextpdf.kernel.colors.ColorConstants;

import org.apache.poi.xwpf.usermodel.*;
import org.apache.poi.openxml4j.opc.PackagePart;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.List;

@Service
public class AlternativePdfService {

    public byte[] convertWordToPdfWithPOI(MultipartFile file) throws Exception {
        try {
            XWPFDocument wordDocument = new XWPFDocument(file.getInputStream());
            ByteArrayOutputStream pdfOutputStream = new ByteArrayOutputStream();
            PdfWriter writer = new PdfWriter(pdfOutputStream);
            PdfDocument pdfDoc = new PdfDocument(writer);
            Document pdfDocument = new Document(pdfDoc);
            PdfFont font;
            try {
                font = PdfFontFactory.createFont(StandardFonts.HELVETICA, PdfFontFactory.EmbeddingStrategy.FORCE_EMBEDDED);
            } catch (Exception e) {
                font = PdfFontFactory.createFont(StandardFonts.HELVETICA);
                System.out.println("POI: Standart font kullanılıyor");
            }
            int paragraphCount = 0;
            for (XWPFParagraph paragraph : wordDocument.getParagraphs()) {
                String paragraphText = paragraph.getText();
                if (paragraphText != null && !paragraphText.trim().isEmpty()) {
                    Paragraph pdfParagraph = new Paragraph(paragraphText);
                    pdfParagraph.setFont(font);
                    pdfParagraph.setFontSize(11);
                    if (paragraph.getAlignment() != null) {
                        switch (paragraph.getAlignment()) {
                            case CENTER:
                                pdfParagraph.setTextAlignment(TextAlignment.CENTER);
                                break;
                            case RIGHT:
                                pdfParagraph.setTextAlignment(TextAlignment.RIGHT);
                                break;
                            case BOTH:
                                pdfParagraph.setTextAlignment(TextAlignment.JUSTIFIED);
                                break;
                            default:
                                pdfParagraph.setTextAlignment(TextAlignment.LEFT);
                        }
                    }
                    
                    pdfDocument.add(pdfParagraph);
                    paragraphCount++;
                }
            }
            int imageCount = 0;
            for (XWPFPictureData picture : wordDocument.getAllPictures()) {
                try {
                    byte[] imageBytes = picture.getData();
                    if (imageBytes != null && imageBytes.length > 0) {
                        Image pdfImage = new Image(ImageDataFactory.create(imageBytes));
                        
                        float width = pdfImage.getImageWidth();
                        float height = pdfImage.getImageHeight();
                        if (width > 500) {
                            float ratio = 500f / width;
                            width = 500;
                            height *= ratio;
                        }
                        if (height > 400) {
                            float ratio = 400f / height;
                            height = 400;
                            width *= ratio;
                        }
                        pdfImage.setWidth(width);
                        pdfImage.setHeight(height);
                        
                        pdfImage.setHorizontalAlignment(com.itextpdf.layout.properties.HorizontalAlignment.CENTER);
                        
                        pdfDocument.add(pdfImage);
                        imageCount++;
                    }
                } catch (Exception e) {
                }
            }
            pdfDocument.close();
            return pdfOutputStream.toByteArray();

        } catch (Exception e) {
            throw new Exception("POI conversion failed: " + e.getMessage(), e);
        }
    }
}