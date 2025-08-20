package com.example.demo.model;

import jakarta.persistence.*;
import org.docx4j.model.properties.paragraph.PBorderRight;
import lombok.*;
import java.util.Date;

@Entity
@Table(name = "files")
@Data
@AllArgsConstructor
@NoArgsConstructor
public class File {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    private String fileName;
    private String fileType;
    private Date createdDate;
    private Long userId;

    @Lob
    private byte[] data;

}
