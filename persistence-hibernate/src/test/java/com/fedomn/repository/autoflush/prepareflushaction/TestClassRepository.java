package com.fedomn.repository.autoflush.prepareflushaction;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface TestClassRepository extends JpaRepository<TestClass, String> {}
