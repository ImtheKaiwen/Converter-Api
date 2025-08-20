package com.example.demo.controller.resource;
import java.util.Date;
import lombok.*;
@Data
@AllArgsConstructor
public class FileResource {
    private Long fileId;
    private String fileName;
    private Date createdDate;
}
