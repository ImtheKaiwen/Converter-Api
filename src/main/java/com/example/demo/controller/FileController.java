package com.example.demo.controller;

import com.example.demo.controller.resource.FileResource;
import com.example.demo.controller.resource.UserResource;
import com.example.demo.model.File;
import com.example.demo.service.FileService;
import jakarta.servlet.http.HttpSession;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.Date;
import java.util.List;

@RestController
@RequestMapping("api/files")
public class FileController {
    public FileService fileService;
    public static final Logger logger = LoggerFactory.getLogger(UserController.class);
    public FileController(FileService fileService) {
        this.fileService = fileService;
    }

    @GetMapping
    public List<File> getFiles(HttpSession session) {
            UserResource userResource = (UserResource) session.getAttribute("currentUser");
            if (userResource == null) {
                logger.error("UserResource is null");
                return null;
            }
            List<File> files = fileService.findByUserId(userResource.getId());
            if (files.isEmpty()) {
                logger.error("UserResource is empty");
                return null;
            }
            return files;
    }

    @PostMapping
    public File uploadFile(@RequestParam("file") MultipartFile file, HttpSession session) {

            UserResource userResource = (UserResource) session.getAttribute("currentUser");
            if (userResource == null) {
                logger.error("UserResource is null");
                return null;
            }

            try {
                File newFile = new File();
                Date createdDate = new Date();
                newFile.setCreatedDate(createdDate);
                newFile.setFileName(file.getOriginalFilename());
                newFile.setFileType(file.getContentType());
                newFile.setData(file.getBytes());
                newFile.setUserId(userResource.getId());

                File savedFile = fileService.save(newFile);
                if (savedFile != null) {
                    logger.info("File saved successfully");
                    return savedFile;
                } else {
                    logger.error("File save failed");
                    return null;
                }
            }catch (Exception e){
                logger.error(e.getMessage());
                e.printStackTrace();
                return null;
            }
    }

    @PostMapping("/convert")
    public FileResource convertFile(@RequestParam("file") MultipartFile file, HttpSession session) {
            UserResource userResource = (UserResource) session.getAttribute("currentUser");
            if (userResource == null) {
                logger.error("UserResource is null");
                return null;
            }
            String fileName = file.getOriginalFilename();
            if (!fileName.endsWith(".doc")  && !fileName.endsWith(".docx")) {
                logger.error("File name not supported");
                return null;
            }
            try {
                byte[] pdfBytes = fileService.convertWordToPdf(file);
                File savedFile = new File();
                Date createdDate = new Date();
                savedFile.setCreatedDate(createdDate);
                savedFile.setFileName(fileName.replaceAll("\\.docx?$", ".pdf"));
                savedFile.setFileType("application/pdf");
                savedFile.setData(pdfBytes);
                savedFile.setUserId(userResource.getId());

                File repositoryFile = fileService.save(savedFile);
                if (repositoryFile != null) {
                    logger.info("File saved successfully");
                    return new FileResource(repositoryFile.getId(), repositoryFile.getFileName(), repositoryFile.getCreatedDate());
                } else {
                    logger.error("File save failed");
                    return null;
                }
            }catch (Exception e){
                logger.error(e.getMessage());
                e.printStackTrace();
                return null;
            }
    }

    @GetMapping("/download/{id}")
    public byte[] downloadFile(@PathVariable("id") Long id, HttpSession session) {
            UserResource userResource = (UserResource) session.getAttribute("currentUser");
            if (userResource == null) {
                logger.error("UserResource is null");
                return null;
            }
            File file = fileService.getFileByIdAndUserId(id, userResource.getId());
            if (file == null) {
                logger.error("File not found");
                return null;
            }
            logger.info("File download successfully");
            return file.getData();
    }
}
