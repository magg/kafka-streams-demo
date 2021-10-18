package com.example.demo;

import com.google.protobuf.Message;
import com.google.protobuf.util.JsonFormat;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import org.apache.commons.io.FileUtils;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;

public class JsonProtoUtils {

  /**
   * Read a proto from a Json file
   */
  public static <T extends Message.Builder> T readFromJson(
      String jsonPath, T builder, JsonFormat.Parser protobufParser) throws IOException {
    Resource resource = new ClassPathResource(jsonPath);
    String jsonEvent = FileUtils.readFileToString(resource.getFile(),
        String.valueOf(StandardCharsets.UTF_8));
    protobufParser.merge(jsonEvent, builder);
    return builder;
  }


  public static <T extends Message.Builder> T readFromJsonReplaceId(
      String jsonPath, T builder, JsonFormat.Parser protobufParser, String id) throws IOException {
    Resource resource = new ClassPathResource(jsonPath);
    String jsonEvent = FileUtils.readFileToString(resource.getFile(),
        String.valueOf(StandardCharsets.UTF_8));

    jsonEvent = jsonEvent.replaceAll("TO_REPLACE", id);

    protobufParser.merge(jsonEvent, builder);
    return builder;
  }
}
