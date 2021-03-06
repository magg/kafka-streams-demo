package com.example.demo.config;

import com.google.protobuf.MessageLite;
import org.apache.kafka.streams.KeyValue;
import org.apache.kafka.streams.kstream.Transformer;
import org.apache.kafka.streams.kstream.TransformerSupplier;
import org.apache.kafka.streams.processor.ProcessorContext;
import org.springframework.kafka.support.serializer.DelegatingSerializer;

public class DeserializationHeaderRemoverTransformerSupplier<K, V extends MessageLite>
    implements TransformerSupplier<K, V, KeyValue<K, V>> {
  @Override
  public Transformer<K, V, KeyValue<K, V>> get() {
    return new Transformer<K, V, KeyValue<K, V>>() {

      private ProcessorContext processorContext;

      @Override
      public void init(ProcessorContext context) {
        this.processorContext = context;
      }

      @Override
      public KeyValue<K, V> transform(K key, V value) {
        this.processorContext.headers().remove(DelegatingSerializer.KEY_SERIALIZATION_SELECTOR);
        return KeyValue.pair(key, value);
      }

      @Override
      public void close() {

      }
    };
  }
}
