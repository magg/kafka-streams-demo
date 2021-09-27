package com.example.demo.config;

import com.google.protobuf.util.JsonFormat;
import java.nio.charset.StandardCharsets;
import java.util.Collections;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Scope;
import org.springframework.http.MediaType;
import org.springframework.http.converter.protobuf.ProtobufJsonFormatHttpMessageConverter;

/**
 * Code taken from https://github.com/klar-mx/operation-support-service/blob/master/service/
 * src/main/java/mx/klar/operation/support/configuration/SerializationConfig.java.
 */
@Configuration
public class SerializationConfig {

  /**
   * Bean for serialization of proto objects coming from http request.
   */
  @Bean
  @Scope(ConfigurableBeanFactory.SCOPE_PROTOTYPE)
  public JsonFormat.Printer protobufJsonFormatPrinter() {
    return JsonFormat.printer().includingDefaultValueFields();
  }

  /**
   * Bean for deserialization of proto objects coming from http request.
   */
  @Bean
  @Scope(ConfigurableBeanFactory.SCOPE_PROTOTYPE)
  public JsonFormat.Parser protobufJsonParser() {
    return JsonFormat.parser().ignoringUnknownFields();
  }

  /**
   * This allows TransactionList instances to be converted to JSON automatically, when used
   * as the return types.
   */
  @Bean
  public ProtobufJsonFormatHttpMessageConverter protobufHttpMessageConverter(
      final JsonFormat.Parser protobufJsonFormatParser,
      final JsonFormat.Printer protobufJsonFormatPrinter) {

    final ProtobufJsonFormatHttpMessageConverter converter =
        new ProtobufJsonFormatHttpMessageConverter(protobufJsonFormatParser,
            protobufJsonFormatPrinter);

    converter.setSupportedMediaTypes(Collections.singletonList(MediaType.APPLICATION_JSON));
    converter.setDefaultCharset(StandardCharsets.UTF_8);

    return converter;
  }

}
