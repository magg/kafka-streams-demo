package com.example.demo.config;


import static com.example.demo.config.KafkaConfig.COMBINED_EVENTS_TOPIC;

import com.google.protobuf.InvalidProtocolBufferException;
import com.google.protobuf.util.JsonFormat.Printer;
import javax.annotation.Nonnull;
import lombok.extern.slf4j.Slf4j;
import mx.klar.provider.common.proto.TransactionProtos.BalanceSyncEvent;
import mx.klar.provider.common.proto.TransactionProtos.TransactionEvent;
import mx.klar.test.common.Protos.CombinedEvent;
import org.springframework.kafka.annotation.KafkaHandler;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Component
@Slf4j
/*
 @KafkaListener(
 groupId = "${kafka.consumer-group}",
 topics = COMBINED_EVENTS_TOPIC,
 properties = {"auto.offset.reset=earliest"}
 )
*/
public class CombinedTopicConsumer {

  private final Printer printer;

  public CombinedTopicConsumer(Printer printer) {
    this.printer = printer;
  }


  /**
   * Listens to all incoming TransactionEvent messages.
   *
   * @param combinedEvent CombinedEvent message object
   */
  @KafkaHandler public void consumeTransactionEvent(@Nonnull CombinedEvent combinedEvent)
      throws InvalidProtocolBufferException {
    printer.print(combinedEvent);

    log.info(" Receiving transaction {} ", printer.print(combinedEvent));

  }

}

