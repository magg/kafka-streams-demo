package com.example.demo;

import com.example.demo.config.BalanceProducer;
import com.example.demo.config.TransactionProducer;
import com.google.protobuf.util.JsonFormat;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Stream;
import lombok.SneakyThrows;
import mx.klar.balance.common.Protos.BalanceEvent;
import mx.klar.provider.common.proto.TransactionProtos.BalanceSyncEvent;
import mx.klar.provider.common.proto.TransactionProtos.TransactionEvent;
import mx.klar.provider.common.proto.TransactionProtos.TransactionEvent.Builder;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.test.context.ContextConfiguration;


@SpringBootTest(webEnvironment = WebEnvironment.RANDOM_PORT)
@EnableConfigurationProperties
public class DemoApplicationTests {


	@Autowired
	private JsonFormat.Parser protobufParser;

	@Autowired
	private TransactionProducer transactionProducer;

	@Autowired
	private BalanceProducer balanceProducer;

	private List<TransactionEvent> eventList = new ArrayList<>();
	private List<BalanceSyncEvent> eventSyncList = new ArrayList<>();
	private List<BalanceEvent> eventBalanceList = new ArrayList<>();



	List<String> eventFiles = List.of("b1", "b2", "b3", "b4","t1-b", "t2", "t3", "t4-b", "t5", "t6", "t7-b", "t8-b", "t9");


	@SneakyThrows
	@Test
	public void produce() {

		for (String file: eventFiles) {
								String updatedPath = "\\transactions\\" + file + ".json";

								System.out.println(updatedPath);

								if (updatedPath.contains("-b")) {
									BalanceSyncEvent.Builder eventBuilder = JsonProtoUtils.readFromJson(updatedPath, BalanceSyncEvent.newBuilder(),
											protobufParser);
									eventSyncList.add(eventBuilder.build());
								} else if (updatedPath.contains("b")) {
									BalanceEvent.Builder eventBuilder = JsonProtoUtils.readFromJson(updatedPath, BalanceEvent.newBuilder(),
											protobufParser);
									BalanceEvent e = eventBuilder.build();
									eventBalanceList.add(e);
								} else {
									Builder eventBuilder = JsonProtoUtils.readFromJson(updatedPath, TransactionEvent.newBuilder(),
											protobufParser);
									TransactionEvent e = eventBuilder.build();
									eventList.add(e);
								}


		}

		balanceProducer.publish(eventBalanceList.get(0));

		balanceProducer.publish(eventBalanceList.get(1));
		balanceProducer.publish(eventBalanceList.get(2));
		balanceProducer.publish(eventBalanceList.get(3));

		transactionProducer.publishSync(eventSyncList.get(0));
		transactionProducer.publish(eventList.get(0));
		transactionProducer.publish(eventList.get(1));
		transactionProducer.publishSync(eventSyncList.get(1));
		transactionProducer.publish(eventList.get(2));
		transactionProducer.publish(eventList.get(3));
		transactionProducer.publishSync(eventSyncList.get(2));
		transactionProducer.publishSync(eventSyncList.get(3));
		transactionProducer.publish(eventList.get(4));

	}

}
