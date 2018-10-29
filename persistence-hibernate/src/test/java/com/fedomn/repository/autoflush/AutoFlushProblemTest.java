package com.fedomn.repository.autoflush;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import lombok.extern.slf4j.Slf4j;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.test.context.junit4.SpringRunner;

@RunWith(SpringRunner.class)
@DataJpaTest
@Slf4j
public class AutoFlushProblemTest {

  @Autowired private FatherRepository fatherRepository;
  @Autowired private SonRepository sonRepository;
  @Autowired private AttachmentRepository attachmentRepository;
  @Autowired private TestEntityManager testEntityManager;
  private String fatherId;

  @Before
  public void setUp() {
    Father father = new Father();
    father.setAttachmentList(Arrays.asList(new Attachment(), new Attachment()));

    father.setSonList(
        Arrays.asList(
            Son.builder().build(),
            Son.builder()
                .attachmentList(Arrays.asList(new Attachment(), new Attachment()))
                .build()));

    fatherRepository.saveAndFlush(father);
    fatherId = father.getId();
    testEntityManager.clear();

    log.info("\033[32m-------------Start Test----------------------------------\033[0m");
  }

  @Test
  public void clearFatherAttachmentAndThenFindAllAttachmentWillCauseImplicitAutoFlush() {
    Father father = fatherRepository.findById(fatherId).orElseThrow(AssertionError::new);
    father.setName("flush test");
    List<Attachment> attachmentList = father.getAttachmentList();
    String fatherSecondAttachmentId = attachmentList.get(0).getId();

    father.getAttachmentList().clear();

    log.info("\033[32m-------------Start findAllById----------------------------------\033[0m");

    // first thinking:

    // when execute HQL query attachment findAllById
    // the HQL query overlap with the queued entity actions (clear attachmentList)
    // so the query will trigger auto flush
    // the trigger log key words is: 'Changes must be flushed to space: attachment'

    // original code analysis:

    // attachmentRepository.findAllById will invoke SessionImpl.java list method
    // the list method will invoke autoFlushIfRequired method
    // and then go to DefaultAutoFlushEventListener.java onAutoFlush method
    // the important judge is flushIsReallyNeeded(event, source)

    // flushIsReallyNeeded judge by source.getActionQueue() querySpace
    // and attachment (attachmentRepository.findAll())

    // this circumstance source.getActionQueue() return:
    // 1. updates that querySpaces is father (father.setName("flush test"))
    // 2. collectionUpdates that querySpaces is attachment (father.getAttachmentList().clear())

    // so collectionUpdates querySpaces overlap with attachment and flushIsReallyNeeded return true
    // so findAllById will trigger auto-flush.

    attachmentRepository.findAllById(Collections.singletonList(fatherSecondAttachmentId));

    log.info("\033[32m-------------End findAllById----------------------------------\033[0m");

    testEntityManager.clear();
    Father foundFather = fatherRepository.findById(fatherId).orElseThrow(AbstractMethodError::new);
    assertThat(foundFather.getName()).isEqualTo("flush test");
    assertThat(foundFather.getAttachmentList().size()).isEqualTo(0);
  }

  @Test
  public void clearFatherAttachmentAndThenFindAttachmentByIdWillNotCauseImplicitAutoFlush() {
    Father father = fatherRepository.findById(fatherId).orElseThrow(AssertionError::new);
    father.setName("flush test");

    father.getAttachmentList().clear();

    log.info("\033[32m-------------Start findById----------------------------------\033[0m");

    // this query not trigger auto-flush
    // because this query is not HQL/JPQL, the query only is query attachment by son id.
    // and SessionImpl.java find method not invoke autoFlushIfRequired
    attachmentRepository.findById("test-id");

    log.info("\033[32m-------------End findById----------------------------------\033[0m");

    testEntityManager.clear();
    Father foundFather = fatherRepository.findById(fatherId).orElseThrow(AbstractMethodError::new);
    assertThat(foundFather.getName()).isNull();
    assertThat(foundFather.getAttachmentList().size()).isEqualTo(2);
  }
}
