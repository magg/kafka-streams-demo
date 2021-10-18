package com.example.demo.extractors;

import mx.klar.balance.common.Protos.BalanceEvent;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.streams.processor.TimestampExtractor;

public class BalanceEventTimestampExtractor implements TimestampExtractor {
  @Override
  public long extract(ConsumerRecord<Object, Object> record, long partitionTime) {
    BalanceEvent balanceEvent = (BalanceEvent) record.value();

    if (balanceEvent != null && balanceEvent.getTimestampInMs() != 0L) {
      return balanceEvent.getTimestampInMs();
    }
    return partitionTime;
  }
}
