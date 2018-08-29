package com.fedomn.stream;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.StringJoiner;
import java.util.function.Supplier;
import java.util.stream.Collector;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import org.junit.Test;

public class StreamTutorial {

  @Test
  public void processing_order() throws Exception {
    Stream.of("A", "B", "C", "D")
        .filter(
            s -> {
              System.out.println("filter: " + s);
              return s.startsWith("A");
            })
        .forEach(s -> System.out.println("forEach: " + s));
  }

  @Test
  public void reused_stream() throws Exception {
    // Java 8 streams cannot be reused. As soon as you call any terminal operation the stream is
    // closed
    // We could create a stream supplier to construct a new stream with all intermediate operations
    // already set up
    Supplier<Stream<String>> streamSupplier =
        () -> Stream.of("d2", "a2", "b1", "b3", "c").filter(s -> s.startsWith("a"));
    assertThat(streamSupplier.get().anyMatch(s -> true)).isTrue();
    assertThat(streamSupplier.get().noneMatch(s -> true)).isFalse();
  }

  @Test
  public void collect_to_list() throws Exception {
    List<String> collect =
        Stream.of("A", "B", "C", "D").filter(s -> s.startsWith("A")).collect(Collectors.toList());
    assertThat(collect.contains("A")).isTrue();
  }

  @Test
  public void collector_of() throws Exception {
    // build our own special collector
    // We have to pass the four ingredients of a collector: a supplier, an accumulator, a combiner
    // and a finisher.
    List<String> testData = Arrays.asList("a1", "b1", "c1", "d1");
    String result = "A1 | B1 | C1 | D1";
    Collector<String, StringJoiner, String> collector =
        Collector.of(
            () -> new StringJoiner(" | "), // supplier
            (j, p) -> j.add(p.toUpperCase()), // accumulator
            StringJoiner::merge, // combiner
            StringJoiner::toString); // finisher
    String collectString = testData.stream().collect(collector);
    assertThat(collectString).isEqualTo(result);
  }

  @Test
  public void flat_map() throws Exception {
    // FlatMap transforms each element of the stream into a stream of other objects
    // So each object will be transformed into zero, one or multiple other objects backed by
    // streams.
    // The contents of those streams will then be placed into the returned stream of the flatMap
    // operation.
    // eg:
    // Stream<String[]>		-> flatMap ->	Stream<String>
    // Stream<Set<String>>	-> flatMap ->	Stream<String>
    // Stream<List<String>>	-> flatMap ->	Stream<String>
    // { {1,2}, {3,4}, {5,6} } -> flatMap -> {1,2,3,4,5,6}
    // { {'a','b'}, {'c','d'}, {'e','f'} } -> flatMap -> {'a','b','c','d','b','b','b'}

    List<List<String>> testData =
        Arrays.asList(Arrays.asList("a", "b", "c"), Arrays.asList("c", "d", "f"));
    List<String> exceptData = Arrays.asList("a", "b", "c", "d", "f");
    List<String> result =
        testData.stream().flatMap(Collection::stream).distinct().collect(Collectors.toList());
    assertThat(result).isEqualTo(exceptData);
  }

  @Test
  public void reduce() throws Exception {
    // The reduction operation combines all elements of the stream into a single result
    List<Integer> testData = Arrays.asList(1, 3, 5);
    Integer result = testData.stream().reduce(0, (sum, each) -> sum += each);
    assertThat(result).isEqualTo(9);

    Optional<Integer> reduce = testData.stream().reduce((s1, s2) -> s1 * s2);
    System.out.println(reduce);
  }

  @Test
  public void parallel_stream() throws Exception {
    // 默认构造的stream是顺序执行的,调用parallel构造并行的stream
    // 注意并行操作中的线程安全
    Arrays.asList("a1", "a2", "b1", "c2", "c1")
        .parallelStream()
        .filter(
            s -> {
              System.out.format("filter: %s [%s]\n", s, Thread.currentThread().getName());
              return true;
            })
        .map(
            s -> {
              System.out.format("map: %s [%s]\n", s, Thread.currentThread().getName());
              return s.toUpperCase();
            })
        .forEach(s -> System.out.format("forEach: %s [%s]\n", s, Thread.currentThread().getName()));
  }
}
