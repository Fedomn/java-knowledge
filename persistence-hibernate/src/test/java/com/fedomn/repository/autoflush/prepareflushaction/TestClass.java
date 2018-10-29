package com.fedomn.repository.autoflush.prepareflushaction;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.GenericGenerator;

@Entity
@Table(name = "test_class")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TestClass {

  @Id
  @GeneratedValue(generator = "customer-uuid")
  @GenericGenerator(name = "customer-uuid", strategy = "org.hibernate.id.UUIDGenerator")
  private String id;
}
