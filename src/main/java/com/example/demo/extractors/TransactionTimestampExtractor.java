package com.example.demo.extractors;

import mx.klar.provider.common.proto.TransactionProtos.TransactionEvent;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.streams.processor.TimestampExtractor;

public class TransactionTimestampExtractor implements TimestampExtractor {

  @Override
  public long extract(ConsumerRecord<Object, Object> record, long partitionTime) {

    if (record.value().getClass().getSimpleName().equals("TransactionEvent")) {
      TransactionEvent transactionEvent = (TransactionEvent) record.value();

      if (transactionEvent != null && transactionEvent.getTimestampInMs() != 0L) {
        return transactionEvent.getTimestampInMs();
      }
    }

    return partitionTime;
  }
}
