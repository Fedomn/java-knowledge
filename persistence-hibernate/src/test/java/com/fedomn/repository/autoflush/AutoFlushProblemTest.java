package com.fedomn.repository.autoflush;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.test.context.junit4.SpringRunner;

@RunWith(SpringRunner.class)
@DataJpaTest
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

    System.out.println("-------------Start Test---------------------");
  }

  @Test
  public void clearFatherAttachmentAndThenFindAllAttachmentWillCauseImplicitAutoFlush() {
    Father father = fatherRepository.findById(fatherId).orElseThrow(AssertionError::new);
    father.setName("flush test");
    List<Attachment> attachmentList = father.getAttachmentList();
    String fatherSecondAttachmentId = attachmentList.get(0).getId();

    father.getAttachmentList().clear();

    // when execute HQL query attachment findAllById
    // the HQL query overlap with the queued entity actions (clear attachmentList)
    // so the query will trigger auto flush

    // the trigger log key words is: 'Changes must be flushed to space: attachment'
    attachmentRepository.findAllById(Collections.singletonList(fatherSecondAttachmentId));

    testEntityManager.clear();
    Father foundFather = fatherRepository.findById(fatherId).orElseThrow(AbstractMethodError::new);
    assertThat(foundFather.getName()).isEqualTo("flush test");
    assertThat(foundFather.getAttachmentList().size()).isEqualTo(0);
  }

  @Test
  public void clearFatherAttachmentAndThenFindAllSonAttachmentWillNotCauseImplicitAutoFlush() {
    Father father = fatherRepository.findById(fatherId).orElseThrow(AssertionError::new);
    father.setName("flush test");

    father.getAttachmentList().clear();

    // this query not trigger auto-flush
    // because this query is no overlaps with father attachment modified actions (clear attachment)
    System.out.println(father.getSonList().get(0).getAttachmentList());
    System.out.println(father.getSonList().get(1).getAttachmentList());

    testEntityManager.clear();
    Father foundFather = fatherRepository.findById(fatherId).orElseThrow(AbstractMethodError::new);
    assertThat(foundFather.getName()).isNull();
    assertThat(foundFather.getAttachmentList().size()).isEqualTo(2);
  }
}
