package com.example.demo.controller;

import com.example.demo.model.File;
import com.example.demo.model.CurrentUser;
import com.example.demo.service.FileService;
import org.apache.coyote.Response;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import jakarta.servlet.http.HttpSession;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("api/files")
public class FileController {
    public FileService fileService;
    public FileController(FileService fileService) {
        this.fileService = fileService;
    }

    @GetMapping
    public ResponseEntity<?> getFiles(HttpSession session) {
        try{

            CurrentUser currentUser = (CurrentUser) session.getAttribute("currentUser");
            if (currentUser == null) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(Map.of("success", false, "message", "User not logged in"));
            }

            // hangi kullanıcı giriş yaptıysa onun dosyaları gelsin
            List<File> files = fileService.findByUserId(currentUser.getId());
            if (files.isEmpty()) {
                System.out.println("No files found for user: " + currentUser.getId());
                return ResponseEntity.ok(Map.of("success", true, "files", files, "message", "No files found"));
            }
            return ResponseEntity.ok(Map.of("success", true, "files", files));
        }catch (Exception e){
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(Map.of("success" , false, "message", e.getMessage()));
        }
    }

    @PostMapping
    public ResponseEntity<?> uploadFile(@RequestParam("file") MultipartFile file, HttpSession session) {
        try{
            CurrentUser currentUser = (CurrentUser) session.getAttribute("currentUser");
            if (currentUser == null) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(Map.of("success", false, "message", "User not logged in"));
            }
            
            File newFile = new File();
            Date createdDate = new Date();
            newFile.setCreatedDate(createdDate);
            newFile.setFileName(file.getOriginalFilename());
            newFile.setFileType(file.getContentType());
            newFile.setData(file.getBytes());
            newFile.setUserId(currentUser.getId());

            File savedFile = fileService.save(newFile);
            if (savedFile != null) {
                return ResponseEntity.status(HttpStatus.CREATED).body(Map.of("success", true, "file", savedFile));
            }
            else{
                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(Map.of("success", false, "message", "File not saved"));
            }
        }catch (Exception e){
            System.out.println("Error : " + e.getMessage());
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(Map.of("success", false, "message", e.getMessage()));
        }
    }

    @PostMapping("/convert")
    public ResponseEntity<?> convertFile(@RequestParam("file") MultipartFile file, HttpSession session) {
        try{
            CurrentUser currentUser = (CurrentUser) session.getAttribute("currentUser");
            if (currentUser == null) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(Map.of("success", false, "message", "User not logged in"));
            }
            
            String fileName = file.getOriginalFilename();
            if (!fileName.endsWith(".doc")  && !fileName.endsWith(".docx")) {
                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(Map.of("success", false, "message", "File format not supported"));
            }

            byte[] pdfBytes = fileService.convertWordToPdf(file);

            File savedFile = new File();
            Date createdDate = new Date();
            savedFile.setCreatedDate(createdDate);
            savedFile.setFileName(fileName.replaceAll("\\.docx?$" , ".pdf"));
            savedFile.setFileType("application/pdf");
            savedFile.setData(pdfBytes);
            savedFile.setUserId(currentUser.getId());
            
            File repositoryFile = fileService.save(savedFile);
            if (repositoryFile != null) {
                Map<String, Object> response = new HashMap<>();
                response.put("success", true);
                response.put("fileId", repositoryFile.getId());
                response.put("fileName", repositoryFile.getFileName());
                response.put("createdDate", repositoryFile.getCreatedDate());
                return ResponseEntity.ok(response);
            } else {
                Map<String, Object> response = new HashMap<>();
                response.put("success", false);
                response.put("message", "File not saved");
                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
            }

        }catch (Exception e){
            System.out.println("Error : " + e.getMessage());
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(Map.of("success", false, "message", e.getMessage()));
        }
    }

    @GetMapping("/download/{id}")
    public ResponseEntity<byte[]> downloadFile(@PathVariable("id") Long id, HttpSession session) {
        try {

            CurrentUser currentUser = (CurrentUser) session.getAttribute("currentUser");
            if (currentUser == null) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
            }
            
            File file = fileService.getFileByIdAndUserId(id, currentUser.getId());
            if (file == null) {
                return ResponseEntity.notFound().build();
            }

            return ResponseEntity.ok()
                    .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + file.getFileName() + "\"")
                    .contentType(MediaType.parseMediaType(file.getFileType()))
                    .body(file.getData());
        } catch (Exception e) {
            System.err.println("Download error: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }
    }
}
