package com.example.demo.config;

import static mx.klar.balance.common.constants.KafkaTopics.BALANCE_EVENTS_TOPIC;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import mx.klar.balance.common.Protos.BalanceEvent;
import mx.klar.provider.common.proto.TransactionProtos.TransactionEvent;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.SendResult;
import org.springframework.stereotype.Service;
import org.springframework.util.concurrent.ListenableFutureCallback;

@Service
@Slf4j
@RequiredArgsConstructor
public class BalanceProducer {


  private final KafkaTemplate<String, BalanceEvent> kafkaTemplate;

  /**
   * Sends {@link TransactionEvent} instance to NotificationCommands kafka topic.
   */
  public void publish(final BalanceEvent event) {
    kafkaTemplate.send(BALANCE_EVENTS_TOPIC, event.getUserId() ,event)
        .addCallback(new ProducedKafkaMessageStatusLogger(
            BALANCE_EVENTS_TOPIC,
            "BalanceEvent"));
  }

  @RequiredArgsConstructor
  @Slf4j
  public static class ProducedKafkaMessageStatusLogger<T> implements
      ListenableFutureCallback<SendResult<String, T>> {

    private final String topic;
    private final String messageType;

    @Override
    public void onFailure(final Throwable ex) {
      log.error(
          String.format("process=send_message, status=failed, topic=%s, messageType=%s",
              topic,
              messageType),
          ex);
    }

    @Override
    public void onSuccess(final SendResult<String, T> result) {
      log.debug("process='{}', status=success, topic='{}', partition='{}', key='{}', offset='{}'",
          messageType,
          result.getProducerRecord().topic(),
          result.getProducerRecord().partition(),
          result.getProducerRecord().key(),
          result.getRecordMetadata().offset());
    }
  }
}
