package com.example.demo.service;

import com.example.demo.model.File;
import com.example.demo.repository.FileRepository;
import org.docx4j.Docx4J;
import org.docx4j.openpackaging.packages.WordprocessingMLPackage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import org.docx4j.convert.out.pdf.viaXSLFO.*;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.util.List;

@Service
public class FileService {

    @Autowired
    private FileRepository fileRepository;

    @Autowired
    private AlternativePdfService alternativePdfService;

    public FileService(FileRepository fileRepository) {
        this.fileRepository = fileRepository;
    }

    public List<File> findAll() {
        return fileRepository.findAll();
    }

    public List<File> findByUserId(Long userId) {
        return fileRepository.findByUserId(userId);
    }

    public File save(File file) {
        return fileRepository.save(file);
    }

    public byte[] convertWordToPdf(MultipartFile file) throws Exception {
        if (file == null || file.isEmpty()) {
            throw new IllegalArgumentException("Dosya boş veya null olamaz.");
        }

        String originalFilename = file.getOriginalFilename();
        if (originalFilename != null &&
                !originalFilename.toLowerCase().endsWith(".docx") &&
                !originalFilename.toLowerCase().endsWith(".doc")) {
            throw new IllegalArgumentException("Sadece .docx veya .doc dosyaları desteklenir.");
        }

        try (InputStream inputStream = file.getInputStream()) {
            WordprocessingMLPackage wordMLPackage = WordprocessingMLPackage.load(inputStream);

            ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
            Docx4J.toPDF(wordMLPackage, outputStream);

            return outputStream.toByteArray();
        } catch (Exception e) {
            System.out.println("docx4j.toPDF failed, trying POI conversion...");
            try {
                return alternativePdfService.convertWordToPdfWithPOI(file);
            } catch (Exception fallbackException) {
                throw new Exception("Word'den PDF'e dönüşüm sırasında hata oluştu: " + fallbackException.getMessage(), fallbackException);
            }
        }
    }

    public File getFileById(Long id) {
        return fileRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("File with id " + id + " not found"));
    }

    public File getFileByIdAndUserId(Long id, Long userId) {
        File file = fileRepository.findByIdAndUserId(id, userId);
        if (file == null) {
            throw new RuntimeException("File with id " + id + " not found for user " + userId);
        }
        return file;
    }
}