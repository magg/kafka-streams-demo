package com.example.demo.config;

import org.apache.kafka.streams.KeyValue;
import org.apache.kafka.streams.kstream.Transformer;
import org.apache.kafka.streams.processor.ProcessorContext;
import org.apache.kafka.streams.state.KeyValueStore;

public class PairTransformer<K,V> implements Transformer<K, V, KeyValue<K, Pair<V, V>>> {
  private ProcessorContext context;
  private String storeName;
  private KeyValueStore<K, V> stateStore;

  PairTransformer(String storeName) {
    this.storeName = storeName;
  }

  @Override
  public void init(ProcessorContext context) {
    this.context = context;
    stateStore = (KeyValueStore<K, V>) context.getStateStore(storeName);
  }

  @Override
  public KeyValue<K, Pair<V, V>> transform(K key, V value) {
    if (stateStore.get(key) == null) {
      stateStore.put(key, value); return null;
    }
    KeyValue<K, Pair<V,V>> result = KeyValue.pair(key, new Pair<>(stateStore.get(key), value));
    stateStore.put(key, null);
    return result;
  }

  @Override
  public void close() { }

}
