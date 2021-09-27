package com.example.demo.config;

import static mx.klar.provider.common.TransactionTopics.TRANSACTION_EVENT_TOPIC_NAME;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import mx.klar.provider.common.proto.TransactionProtos.BalanceSyncEvent;
import mx.klar.provider.common.proto.TransactionProtos.TransactionEvent;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.SendResult;
import org.springframework.stereotype.Service;
import org.springframework.util.concurrent.ListenableFutureCallback;

@Service
@Slf4j
@RequiredArgsConstructor
public class TransactionProducer {


  private final KafkaTemplate<String, TransactionEvent> kafkaTemplate;
  private final KafkaTemplate<String, BalanceSyncEvent> kafkaTemplateSync;

  /**
   * Sends {@link TransactionEvent} instance to NotificationCommands kafka topic.
   */
  public void publish(final TransactionEvent event) {
    kafkaTemplate.send(TRANSACTION_EVENT_TOPIC_NAME, event.getSource().getInternalId(), event)
        .addCallback(new ProducedKafkaMessageStatusLogger(
            TRANSACTION_EVENT_TOPIC_NAME,
            "TransactionEvent"));
  }

  public void publishSync(final BalanceSyncEvent event) {
    kafkaTemplateSync.send(TRANSACTION_EVENT_TOPIC_NAME, event.getAccountId().getInternalId(), event)
        .addCallback(new ProducedKafkaMessageStatusLogger(
            TRANSACTION_EVENT_TOPIC_NAME,
            "BalanceSyncEvent"));
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
