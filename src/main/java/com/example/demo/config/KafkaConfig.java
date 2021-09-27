package com.example.demo.config;

import static com.example.demo.config.StreamConstants.STRING_SERDE;
import static mx.klar.balance.common.constants.KafkaTopics.BALANCE_EVENTS_TOPIC;
import static mx.klar.kafka.spring.config.KafkaProtoClassUtil.getProtoClassHeaderLabel;
import static mx.klar.kafka.spring.config.KafkaProtoClassUtil.getProtoClassParser;
import static mx.klar.kafka.spring.config.KafkaProtoClassUtil.getProtoClassesInPaths;

import com.github.daniel.shuy.kafka.protobuf.serde.KafkaProtobufDeserializer;
import com.github.daniel.shuy.kafka.protobuf.serde.KafkaProtobufSerializer;
import com.google.common.collect.ImmutableList;
import java.time.Duration;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import lombok.Data;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import mx.klar.balance.common.Protos.BalanceEvent;
import mx.klar.provider.common.proto.TransactionProtos.TransactionEvent;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.common.serialization.Serdes;
import org.apache.kafka.streams.KafkaStreams;
import org.apache.kafka.streams.KeyValue;
import org.apache.kafka.streams.StreamsBuilder;
import org.apache.kafka.streams.StreamsConfig;
import org.apache.kafka.streams.kstream.Consumed;
import org.apache.kafka.streams.kstream.GlobalKTable;
import org.apache.kafka.streams.kstream.Grouped;
import org.apache.kafka.streams.kstream.JoinWindows;
import org.apache.kafka.streams.kstream.Joined;
import org.apache.kafka.streams.kstream.KStream;
import org.apache.kafka.streams.kstream.KTable;
import org.apache.kafka.streams.kstream.Materialized;
import org.apache.kafka.streams.kstream.Produced;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.annotation.KafkaStreamsDefaultConfiguration;
import org.springframework.kafka.config.KafkaStreamsConfiguration;
import org.springframework.kafka.config.StreamsBuilderFactoryBeanCustomizer;
import org.springframework.kafka.support.serializer.DelegatingDeserializer;
import org.springframework.kafka.support.serializer.DelegatingSerializer;

import com.google.protobuf.MessageLite;
import org.apache.commons.lang3.tuple.Pair;

import static mx.klar.provider.common.TransactionTopics.TRANSACTION_EVENT_TOPIC_NAME;


@Configuration
@Slf4j
public class KafkaConfig {

  @Value("${local.stream.store}") private String stateStoreName;
  @Value("${local.stream.input}") private String inputTopic;
  @Value("${local.stream.output}") private String outputTopic;
  @Value("${local.stream.name}") private String streamingAppName;
  @Value("${local.kafka.bootstrap-servers}") private String bootStrapServers;


  @Data public static class Proto {
    /// The base path used to search for Protocol Buffer classes to set up.
    private List<String> basePaths = ImmutableList.of("mx.klar", "com.klar");
  }


  private Proto proto;
  private final Emailer emailer;
  private final Mailer mailer;

  public KafkaConfig() {
    this.emailer = new LoggingEmailer();
    this.mailer = new TestMailer();

    proto = new Proto();
  }

  @Autowired private PairTransformerSupplier<String> pairTransformerSupplier;

  @Setter
  private JoinWindows resultEventJoinWindow = JoinWindows.of(Duration.ofDays(2));



  @Bean
  KafkaStreams createUserProviderAccountCommandAndResultJoinedStream(
      StreamsBuilder streamsBuilder) {
    // the two topics has to be co-partitioned here, 128 partitions and same partition
    // key commandId=resultId
    /**
    KTable<String, TransactionEvent> transactions = streamsBuilder.table(TRANSACTION_EVENT_TOPIC_NAME,
        Materialized.with(STRING_SERDE,StreamConstants.TRANSACTION_EVENT_SERDE));

     **/

/**
    GlobalKTable<String, TransactionEvent> transactions = streamsBuilder
        .globalTable(TRANSACTION_EVENT_TOPIC_NAME,
            Materialized.with(STRING_SERDE,StreamConstants.TRANSACTION_EVENT_SERDE));





    KTable<String, TransactionEvent> transactions = streamsBuilder
        .table(TRANSACTION_EVENT_TOPIC_NAME, Consumed.with(STRING_SERDE, StreamConstants.TRANSACTION_EVENT_SERDE))
        .groupBy((key, value) -> KeyValue.pair(value.getId(), value),
            Grouped.with(STRING_SERDE,StreamConstants.TRANSACTION_EVENT_SERDE))
        .aggregate(
            // Initiate the aggregate value
            () -> null,
            // adder (doing nothing, just passing the user through as the value)
            (transactionID, value, aggValue) -> value,
            // subtractor (doing nothing, just passing the user through as the value)
            (transactionID, value, aggValue) -> value,
            Materialized.with(STRING_SERDE,StreamConstants.TRANSACTION_EVENT_SERDE));
 **/


    KStream<String, TransactionEvent> transactions = streamsBuilder.stream(TRANSACTION_EVENT_TOPIC_NAME,
            Consumed.with(STRING_SERDE, StreamConstants.TRANSACTION_EVENT_SERDE))
        .selectKey((s, transactionEvent) -> transactionEvent.getId());


    KStream<String, BalanceEvent> balance = streamsBuilder.stream(BALANCE_EVENTS_TOPIC,
            Consumed.with(STRING_SERDE, StreamConstants.BALANCE_EVENT_SERDE))
        .selectKey((s, balanceEvent) -> balanceEvent.getTransactionEventUpdate().getTransactionEventId().replaceAll("T:", ""));

/**

    KTable<String, BalanceEvent> balance = streamsBuilder
        .table(BALANCE_EVENTS_TOPIC, Consumed.with(STRING_SERDE, StreamConstants.BALANCE_EVENT_SERDE))
        .groupBy((key, value) -> KeyValue.pair(value.getTransactionEventUpdate().getTransactionEventId().replaceAll("T:", ""), value),
            Grouped.with(STRING_SERDE,StreamConstants.BALANCE_EVENT_SERDE))
        .aggregate(
        // Initiate the aggregate value
        () -> null,
        // adder (doing nothing, just passing the user through as the value)
        (transactionID, value, aggValue) -> value,
        // subtractor (doing nothing, just passing the user through as the value)
        (transactionID, value, aggValue) -> value,
            Materialized.with(STRING_SERDE,StreamConstants.BALANCE_EVENT_SERDE)
    );
 **/

    final Joined<String, BalanceEvent, TransactionEvent> serdes =
        Joined.with(STRING_SERDE, StreamConstants.BALANCE_EVENT_SERDE, StreamConstants.TRANSACTION_EVENT_SERDE);


    balance
        .join(transactions,
            (result, trans) -> Pair.of(
             trans, result
            ), resultEventJoinWindow,serdes)
        .peek((key, emailTuple) -> mailer.sendEmail(emailTuple));

    /*

    KTable<String, Object> resultBranches =
        //results.filter((key, value) -> value.getM
    results.join(balanceTable,
        (transaction, balance) -> transaction.getId()
            .equals(balance.getTransactionEventUpdate().getTransactionEventId()));


    resultBranches.toStream().foreach((key, value) -> {
      System.out.println(key + ":" + value);
    });





    transactions.join(balance, EmailTuple::new, JoinWindows.of(Duration.ofDays(2L)))
        .peek((key, emailTuple)
            -> emailer.sendEmail(emailTuple)
        );
 */
    return new KafkaStreams(streamsBuilder.build(),providerAccountStreamAppConfigs().asProperties());
  }

  //@Bean(name = KafkaStreamsDefaultConfiguration.DEFAULT_STREAMS_CONFIG_BEAN_NAME)
  public KafkaStreamsConfiguration providerAccountStreamAppConfigs() {
    Map<String, Object> config = new HashMap<>();
    config.put(StreamsConfig.APPLICATION_ID_CONFIG, streamingAppName);
    config.put(StreamsConfig.BOOTSTRAP_SERVERS_CONFIG, bootStrapServers);
    config.put(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, "earliest");
    config.put(StreamsConfig.DEFAULT_KEY_SERDE_CLASS_CONFIG, Serdes.StringSerde.class);
    config.put(StreamsConfig.PROCESSING_GUARANTEE_CONFIG, StreamsConfig.EXACTLY_ONCE);
    return new KafkaStreamsConfiguration(config);
  }



  /**
   * Uncaught exception handler.
   *
   * @return
   */
  @Bean public StreamsBuilderFactoryBeanCustomizer uncaughtExceptionHandler() {
    return factoryBean -> factoryBean.setUncaughtExceptionHandler((threadId, throwable) -> {
      log.error("Uncaught exception in stream app in thread {}", threadId, throwable);
    });
  }

  /**
   * create state transition logger.
   *
   * @return
   */
  @Bean public StreamsBuilderFactoryBeanCustomizer stateTransitionLogger() {
    return factoryBean -> factoryBean.setStateListener((newState, oldState) -> {
      log.info("State transition from {} to {}", oldState, newState);
    });
  }

  /**
   * Constructs a DelegatingDeserializer which selects and uses a deserializer based on
   * the header value indicating the Protocol Buffer class.
   */
  public DelegatingDeserializer buildDelegatingDeserializer() {
    // Detect our candidate Protocol Buffer message types to use.
    Set<Class<? extends MessageLite>> protoClasses = getProtoClassesInPaths(proto.getBasePaths());

    // Map the Message header values to a deserializer.
    Map<String, Object> deserializers = new HashMap<>();
    for (Class<? extends MessageLite> protoClass : protoClasses) {
      deserializers.put(getProtoClassHeaderLabel(protoClass),
          new KafkaProtobufDeserializer<>(getProtoClassParser(protoClass)));
    }

    Map<String, Object> configs = new HashMap<>();
    configs.put(DelegatingSerializer.VALUE_SERIALIZATION_SELECTOR_CONFIG, deserializers);

    DelegatingDeserializer deserializer = new DelegatingDeserializer();
    deserializer.configure(configs, false);

    return deserializer;
  }


  /**
   * Constructs a DelegatingSerializer which serializes Protocol Buffer classes based on the type
   * in the message header.
   */
  public DelegatingSerializer buildDelegatingSerializer() {
    // Detect our candidate Protocol Buffer message types to use.
    Set<Class<? extends MessageLite>> protoClasses = getProtoClassesInPaths(proto.getBasePaths());

    // Create a mapping between header values and actual serializers.
    Map<String, Object> serializers = new HashMap<>();
    for (Class<? extends MessageLite> protoClass : protoClasses) {
      serializers.put(getProtoClassHeaderLabel(protoClass), new KafkaProtobufSerializer<>());
    }

    Map<String, Object> configs = new HashMap<>();
    configs.put(DelegatingSerializer.VALUE_SERIALIZATION_SELECTOR_CONFIG, serializers);

    DelegatingSerializer serializer = new DelegatingSerializer();
    serializer.configure(configs, false);

    return serializer;
  }

  public class MySerde extends Serdes.WrapperSerde<Object> {
    public MySerde() {
      super(buildDelegatingSerializer(), buildDelegatingDeserializer());
    }
  }



  private static class LoggingEmailer implements Emailer {

    @Override public void sendEmail(final EmailTuple details) {
      //In a real implementation we would do something a little more useful
      log.warn("Sending email: \nCustomer:{}\nOrder: {}\n", details.transactionEvent, details.balanceEvent);
    }
  }

  interface Emailer {
    void sendEmail(EmailTuple details);
  }

  private static class TestMailer implements Mailer {

    @Override public void sendEmail(final Pair<TransactionEvent, BalanceEvent>  details) {
      //In a real implementation we would do something a little more useful
      log.warn("Sending email: \nCustomer:{}\nOrder: {}\n", details.getLeft(), details.getRight());
    }
  }

  interface Mailer {
    void sendEmail(Pair<TransactionEvent, BalanceEvent> details);
  }


  public static class EmailTuple {


    public TransactionEvent transactionEvent;
    public BalanceEvent balanceEvent;

    public EmailTuple(final BalanceEvent balanceEvent, final TransactionEvent transactionEvent) {
      this.transactionEvent = transactionEvent;
      this.balanceEvent = balanceEvent;
    }

    @Override public String toString() {
      return "EmailTuple{" + "order=" + transactionEvent.toString() + ", payment=" + balanceEvent.toString() + '}';
    }
  }

 }

