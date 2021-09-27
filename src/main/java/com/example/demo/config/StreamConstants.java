package com.example.demo.config;


import com.github.daniel.shuy.kafka.protobuf.serde.KafkaProtobufSerde;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import mx.klar.balance.common.Protos.BalanceEvent;
import mx.klar.provider.common.proto.TransactionProtos.BalanceSyncEvent;
import mx.klar.provider.common.proto.TransactionProtos.TransactionEvent;
import org.apache.kafka.common.serialization.Serde;
import org.apache.kafka.common.serialization.Serdes.StringSerde;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.apache.kafka.common.serialization.StringSerializer;

/**
 * Holds static information related to Kafka Stream processing.
 */
@NoArgsConstructor(access = AccessLevel.PRIVATE)
final class StreamConstants {

  // serdes
  static final StringSerde STRING_SERDE = new StringSerde();

  static final Serde<TransactionEvent> TRANSACTION_EVENT_SERDE =
      new KafkaProtobufSerde<>(TransactionEvent.parser());


  static final Serde<BalanceEvent> BALANCE_EVENT_SERDE =
      new KafkaProtobufSerde<>(BalanceEvent.parser());



  static final Serde<BalanceSyncEvent> BALANCE_SYNC_EVENT_SERDE =
      new KafkaProtobufSerde<>(BalanceSyncEvent.parser());

  // serializer
  static final StringSerializer STRING_SERIALIZER = new StringSerializer();

  // deserializer
  static final StringDeserializer STRING_DESERIALIZER = new StringDeserializer();

}
