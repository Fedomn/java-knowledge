package com.fedomn.repository.autoflush;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface FatherRepository extends JpaRepository<Father, String> {}
