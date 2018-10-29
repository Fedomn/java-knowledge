package com.fedomn.repository.autoflush.prepareflushaction;

import com.fedomn.repository.autoflush.AttachmentRepository;
import com.fedomn.repository.autoflush.Father;
import com.fedomn.repository.autoflush.FatherRepository;
import com.fedomn.repository.autoflush.Son;
import com.fedomn.repository.autoflush.SonRepository;
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
public class PrepareActionQueueTest {

  private String fatherId;

  @Autowired private FatherRepository fatherRepository;
  @Autowired private TestClassRepository testClassRepository;
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

    log.info("\033[32m start findAll \033[0m");
    // will not trigger auto flush
    // but will prepare flush action
    testClassRepository.findAll();
    log.info("\033[32m end findAll \033[0m");

    log.info("\033[32m start save \033[0m");
    son.setName("saved name");
    fatherRepository.save(father);
    log.info("\033[32m end save \033[0m");

    log.info("\033[32m start flush \033[0m");
    // first will insert into son with null (in insert action)
    // second will update son name with saved name
    entityManager.flush();
  }
}
