package com.example.demo.repository;

import com.example.demo.model.File;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface FileRepository extends JpaRepository<File,Long> {
    
    List<File> findByUserId(Long userId);
    
    File findByIdAndUserId(Long id, Long userId);
}
