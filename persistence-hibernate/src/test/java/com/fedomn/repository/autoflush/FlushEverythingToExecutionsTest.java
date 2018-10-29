package com.fedomn.repository.autoflush;

import java.util.Arrays;
import javax.persistence.EntityManager;
import lombok.extern.slf4j.Slf4j;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.transaction.annotation.Transactional;

@RunWith(SpringRunner.class)
@SpringBootTest
@Slf4j
public class FlushEverythingToExecutionsTest {

  private String fatherId;

  @Autowired private FatherRepository fatherRepository;
  @Autowired private SonRepository sonRepository;
  @Autowired private AttachmentRepository attachmentRepository;

  @Autowired private EntityManager entityManager;

  @Before
  public void setUp() {
    Father father = new Father();

    father.setSonList(
        Arrays.asList(Son.builder().name("s1").build(), Son.builder().name("s2").build()));

    fatherRepository.saveAndFlush(father);
    fatherId = father.getId();
    entityManager.clear();

    log.info("\033[32m-------------Start Test----------------------------------\033[0m");
  }

  @Test
  @Transactional
  public void hibernateListWillFlushEverythingToExecutionsTest() {
    Father father = fatherRepository.findById(fatherId).orElseThrow(AssertionError::new);
    Son son = Son.builder().build();
    father.getSonList().add(son);

    log.info("\033[32m start findAll and will trigger flush \033[0m");

    // will insert son with null name
    // because in DefaultAutoFlushEventListener#onAutoFlush method
    // flushEverythingToExecutions(source) will prepare entity flush
    attachmentRepository.findAll();

    log.info("\033[32m start findAll and will trigger flush \033[0m");
    son.setName("saved name");

    fatherRepository.save(father);
    log.info("\033[32m start flush \033[0m");
    // will update son name and flush
    entityManager.flush();
  }
}
