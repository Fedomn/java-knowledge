package com.fedomn.reference;

import static org.assertj.core.api.Assertions.assertThat;

import java.lang.ref.PhantomReference;
import java.lang.ref.ReferenceQueue;
import java.lang.ref.SoftReference;
import java.lang.ref.WeakReference;
import java.util.Map;
import java.util.WeakHashMap;
import org.junit.Test;

/**
 * 四种Java引用(强 > 软 > 弱 > 虚)
 *
 * <p>
 *
 * 强引用
 * 强引用可以直接访问目标对象，它会尽可能长时间的存活于 JVM 内，当没有任何对象指向它时 GC 执行后将会被回收。
 * 当内存空间不足，Java虚拟机宁愿抛出OutOfMemoryError错误，使程序异常终止，
 * 也不会靠随意回收具有强引用的对象来解决内存不足的问题
 * String StrongReference = new String("StrongReference");
 *
 * <p>
 *
 * 软引用
 * 如果一个对象只具有软引用，则内存空间足够，垃圾回收器就不会回收它；如果内存空间不足了，就会回收这些对象的内存
 * String sf = new String("SoftReference");
 * ReferenceQueue queue = new ReferenceQueue();
 * 如果软引用所引用的对象被垃圾回收器回收，Java虚拟机就会把这个软引用加入到与之关联的引用队列中
 * SoftReference<String> softReference = new SoftReference<String>(sf, queue);
 * sf = null;//清除强引用
 *
 * <p>
 *
 * 弱引用
 * 弱引用与软引用的区别在于：只具有弱引用的对象拥有更短暂的生命周期
 * 当所引用的对象在 JVM 内不再有强引用时，不管当前内存空间足够与否，都会回收它的内存
 * String wf = new String("WeakReference");
 * ReferenceQueue queue = new ReferenceQueue();
 * WeakReference<String> weakReference = new WeakReference<String>(wf, queue);
 * wf = null;//清除强引用
 *
 * <p>
 *
 * 虚引用
 * 虚引用并不会决定对象的生命周期。如果一个对象仅持有虚引用，那么它就和没有任何引用一样，在任何时候都可能被垃圾回收器回收
 * 它的 get() 方法永远返回 null.虚引用必须和引用队列联合使用。
 * 当垃圾回收器准备回收一个对象时，如果发现它还有虚引用，就会在回收对象的内存之前，把这个虚引用加入到与之关联的引用队列中
 * String pr = new String("PhantomReference");
 * ReferenceQueue<String> queue = new ReferenceQueue<String>();
 * PhantomReference<String> reference = new PhantomReference<String>(pr, queue);
 */
public class FourJavaReference {

  @Test
  public void test_strong_reference() {
    Object reference = new Object();
    // 赋值创建强引用
    Object strongReference = reference;
    assertThat(reference).isEqualTo(strongReference);

    reference = null;
    System.gc();

    // 强引用在GC后不会被回收
    assertThat(strongReference).isNotNull();
  }

  @Test
  public void test_soft_reference() throws Exception {
    Object reference = new Object();
    SoftReference<Object> softReference = new SoftReference<Object>(reference);

    assertThat(softReference.get()).isNotNull();

    reference = null;
    System.gc();

    // 软引用只用在 JVM内存不足的时候 才会被 回收
    assertThat(softReference.get()).isNotNull();
  }

  @Test
  public void test_weak_reference() throws Exception {
    Object reference = new Object();
    WeakReference<Object> weakReference = new WeakReference<Object>(reference);

    assertThat(reference).isEqualTo(weakReference.get());

    reference = null;
    System.gc();

    // 一旦没有指向reference的强引用，弱引用将会被GC回收
    assertThat(weakReference.get()).isNull();
  }

  @Test
  public void test_WeakHashMap() throws Exception {
    Map<Object, Object> weakHashMap = new WeakHashMap<Object, Object>();
    Object key = new Object();
    Object value = new Object();
    weakHashMap.put(key, value);

    assertThat(weakHashMap.containsValue(value)).isTrue();

    key = null;
    System.gc();

    Thread.sleep(1000);

    // 一旦没有指向 key 的强引用, WeakHashMap 在 GC 后将自动删除相关的 entry
    assertThat(weakHashMap.containsValue(value)).isFalse();
  }

  @Test
  public void test_phantom_reference() throws Exception {
    Object referent = new Object();
    PhantomReference<Object> phantomReference =
        new PhantomReference<Object>(referent, new ReferenceQueue<Object>());

    // 虚引用的get方法永远返回 null
    assertThat(phantomReference.get()).isNull();
  }
}
