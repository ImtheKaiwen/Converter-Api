package com.example.demo.service;

import com.example.demo.model.File;
import com.example.demo.repository.FileRepository;
import org.docx4j.convert.out.pdf.viaXSLFO.PdfSettings;
import org.docx4j.openpackaging.packages.WordprocessingMLPackage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import org.docx4j.convert.out.pdf.PdfConversion;
import org.docx4j.convert.out.pdf.viaXSLFO.Conversion;
import org.docx4j.Docx4J;



import java.io.ByteArrayOutputStream;
import java.util.List;

@Service
public class FileService {
    @Autowired
    public FileRepository fileRepository;
    
    @Autowired
    private AlternativePdfService alternativePdfService;

    public FileService(FileRepository fileRepository) {
        this.fileRepository = fileRepository;
    }

    public List<File> findAll(){
        return fileRepository.findAll();
    }
    
    public List<File> findByUserId(Long userId) {
        return fileRepository.findByUserId(userId);
    }

    public File save(File file){
        return fileRepository.save(file);
    }

    public byte[] convertWordToPdf(MultipartFile file) throws Exception {
        try {
            System.out.println("Starting conversion for file: " + file.getOriginalFilename());
            System.out.println("File size: " + file.getSize() + " bytes");
            System.out.println("File content type: " + file.getContentType());
            

            WordprocessingMLPackage wordMLPackage = WordprocessingMLPackage.load(file.getInputStream());
            System.out.println("Word document loaded successfully");
            

            ByteArrayOutputStream os = new ByteArrayOutputStream();
            

            Docx4J.toPDF(wordMLPackage, os);
            
            System.out.println("Successfully converted word to PDF. Size: " + os.size() + " bytes");
            return os.toByteArray();
        } catch (Exception e) {
            System.err.println("Error during PDF conversion: " + e.getMessage());
            System.err.println("Exception type: " + e.getClass().getSimpleName());
            e.printStackTrace();
            

            try {
                System.out.println("Alternatif");
                WordprocessingMLPackage wordMLPackage = WordprocessingMLPackage.load(file.getInputStream());
                ByteArrayOutputStream os = new ByteArrayOutputStream();
                
                PdfSettings pdfSettings = new PdfSettings();
                pdfSettings.setApacheFopMime("application/pdf");
                
                PdfConversion converter = new Conversion(wordMLPackage);
                converter.output(os, pdfSettings);
                
                System.out.println("Alternative conversion successful. Size: " + os.size() + " bytes");
                return os.toByteArray();
            } catch (Exception e2) {
                System.err.println("Alternative docx4j conversion also failed: " + e2.getMessage());
                

                try {
                    System.out.println("Trying POI + iText conversion as final fallback...");
                    return alternativePdfService.convertWordToPdfWithPOI(file);
                } catch (Exception e3) {
                    System.err.println("POI + iText conversion also failed: " + e3.getMessage());
                    e3.printStackTrace();
                    throw new RuntimeException("PDF conversion failed with all methods. docx4j: " + e.getMessage() + ", POI: " + e3.getMessage(), e);
                }
            }
        }
    }

    public File getFileById(Long id){
        return fileRepository.findById(id).orElseThrow(()-> new RuntimeException("File with id " + id + " not found"));
    }
    
    public File getFileByIdAndUserId(Long id, Long userId) {
        File file = fileRepository.findByIdAndUserId(id, userId);
        if (file == null) {
            throw new RuntimeException("File with id " + id + " not found for user " + userId);
        }
        return file;
    }

}
