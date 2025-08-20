package com.example.demo.controller.resource;
import java.io.Serializable;
import lombok.*;
@Data
@AllArgsConstructor
@NoArgsConstructor
public class UserResource implements Serializable {
    private static final long serialVersionUID = 1L;
    private long id;
    private String firstName;
    private String lastName;
    public String email;
}

