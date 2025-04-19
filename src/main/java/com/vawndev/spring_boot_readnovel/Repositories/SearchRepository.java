package com.vawndev.spring_boot_readnovel.Repositories;

import com.vawndev.spring_boot_readnovel.Entities.Story;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import java.util.List;

public interface SearchRepository extends JpaRepository<Story,String>, JpaSpecificationExecutor<Story> {
    List<Story> findAcceptByid(String title);
}
