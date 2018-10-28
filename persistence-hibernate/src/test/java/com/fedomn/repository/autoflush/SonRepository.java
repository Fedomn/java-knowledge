package com.fedomn.repository.autoflush;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface SonRepository extends JpaRepository<Son, String> {}
