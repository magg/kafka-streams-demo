package com.example.demo.config;

import org.apache.kafka.streams.KeyValue;
import org.apache.kafka.streams.kstream.Transformer;
import org.apache.kafka.streams.kstream.TransformerSupplier;

public class PairTransformerSupplier<V> implements TransformerSupplier<Integer, V, KeyValue<Integer, Pair<V,V>>> {

  private String storeName;

  public PairTransformerSupplier(String storeName) {
    this.storeName = storeName;
  }

  @Override
  public Transformer<Integer, V, KeyValue<Integer, Pair<V, V>>> get() {
    return new PairTransformer<>(storeName);
  }
}
