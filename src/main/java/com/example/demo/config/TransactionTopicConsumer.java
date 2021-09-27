package com.example.demo.config;


import static mx.klar.provider.common.TransactionTopics.TRANSACTION_EVENT_TOPIC_NAME;

import com.google.protobuf.InvalidProtocolBufferException;
import com.google.protobuf.util.JsonFormat;
import com.google.protobuf.util.JsonFormat.Printer;
import javax.annotation.Nonnull;
import lombok.extern.slf4j.Slf4j;
import mx.klar.balance.common.Protos.Balance;
import mx.klar.provider.common.proto.TransactionProtos.BalanceSyncEvent;
import mx.klar.provider.common.proto.TransactionProtos.TransactionEvent;
import org.springframework.kafka.annotation.KafkaHandler;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Component
@Slf4j
/**
 @KafkaListener(
 groupId = "${kafka.consumer-group}",
 topics = TRANSACTION_EVENT_TOPIC_NAME,
 properties = {"auto.offset.reset=earliest"}
 )
 **/
public class TransactionTopicConsumer {

  private final JsonFormat.Printer printer;

  public TransactionTopicConsumer(Printer printer) {
    this.printer = printer;
  }


  /**
   * Listens to all incoming TransactionEvent messages.
   *
   * @param transactionEvent TransactionEvent message object
   */
  @KafkaHandler public void consumeTransactionEvent(@Nonnull TransactionEvent transactionEvent)
      throws InvalidProtocolBufferException {
    printer.print(transactionEvent);

    log.info(" Receiving transaction {} ", printer.print(transactionEvent));

  }

  @KafkaHandler(isDefault = true)
  public void handleUnsupportedMessage(final BalanceSyncEvent balanceSyncEvent)
      throws InvalidProtocolBufferException {
    // silently consume unsupported to move cursor forward

    log.info(" Receiving balance {} ", printer.print(balanceSyncEvent));

  }
}

