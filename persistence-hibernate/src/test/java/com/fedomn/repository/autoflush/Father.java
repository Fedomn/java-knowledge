package com.fedomn.repository.autoflush;

import java.util.ArrayList;
import java.util.List;
import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.ToString;
import org.hibernate.annotations.GenericGenerator;

@Entity
@Table(name = "father")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Father {
  @Id
  @GeneratedValue(generator = "customer-uuid")
  @GenericGenerator(name = "customer-uuid", strategy = "org.hibernate.id.UUIDGenerator")
  private String id;

  private String name;

  @Builder.Default
  @ToString.Exclude
  @EqualsAndHashCode.Exclude
  @OneToMany(fetch = FetchType.LAZY, cascade = CascadeType.ALL)
  @JoinColumn(name = "father_id")
  private List<Attachment> attachmentList = new ArrayList<>();

  @Builder.Default
  @ToString.Exclude
  @EqualsAndHashCode.Exclude
  @OneToMany(cascade = CascadeType.ALL, fetch = FetchType.LAZY)
  @JoinColumn(name = "father_id")
  private List<Son> sonList = new ArrayList<>();
}
