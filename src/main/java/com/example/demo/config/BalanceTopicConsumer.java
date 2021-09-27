package com.example.demo.config;


import static mx.klar.balance.common.constants.KafkaTopics.BALANCE_EVENTS_TOPIC;
import static mx.klar.provider.common.TransactionTopics.TRANSACTION_EVENT_TOPIC_NAME;

import com.google.protobuf.InvalidProtocolBufferException;
import com.google.protobuf.util.JsonFormat.Printer;
import javax.annotation.Nonnull;
import lombok.extern.slf4j.Slf4j;
import mx.klar.balance.common.Protos.BalanceEvent;
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
    topics = BALANCE_EVENTS_TOPIC,
    properties = {"auto.offset.reset=earliest"}
)
**/
public class BalanceTopicConsumer {

  private final Printer printer;

  public BalanceTopicConsumer(Printer printer) {
    this.printer = printer;
  }


  /**
   * Listens to all incoming TransactionEvent messages.
   *
   * @param balanceEvent TransactionEvent message object
   */
  @KafkaHandler public void consumeTransactionEvent(@Nonnull BalanceEvent balanceEvent)
      throws InvalidProtocolBufferException {
    printer.print(balanceEvent);

    log.info(" Receiving balance {} ", printer.print(balanceEvent));

  }

  @KafkaHandler(isDefault = true)
  public void handleUnsupportedMessage(final Object object)
      throws InvalidProtocolBufferException {
    // silently consume unsupported to move cursor forward

    log.info(" Receiving unsupo {} ", object.toString());

  }
}

