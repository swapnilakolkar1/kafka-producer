package com.project.kafka.producer.eventHandler;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.common.header.Header;
import org.apache.kafka.common.header.internals.RecordHeader;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.SendResult;
import org.springframework.stereotype.Component;
import org.springframework.util.concurrent.ListenableFuture;
import org.springframework.util.concurrent.ListenableFutureCallback;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.project.kafka.producer.ResquestBodyModel.BookRequestModel;
import com.project.kafka.producer.configuration.KafkaTopics;

import lombok.extern.slf4j.Slf4j;

@Component
@Slf4j
public class KafkaBookEventProducer {
	@Autowired
	KafkaTemplate<Integer, String> kafkaTemplate;

	@Autowired
	ObjectMapper mapper;

	// Log log = LogFactory.getLog(KafkaBookEventProducer.class);

	public void sendAddBookMessageSync(BookRequestModel book) throws JsonProcessingException {
		String message = mapper.writeValueAsString(book);
		Integer key=book.getBookId();
		log.info("sending kafka message");
		kafkaTemplate.send(KafkaTopics.TOPIC_BOOK, key, message);
		log.info("sent kafka message");
	}

	public void sendAddBookMessageAsync(BookRequestModel book) throws JsonProcessingException {
		String message = mapper.writeValueAsString(book);
		log.info("sending kafka message");
		Integer key=book.getBookId();
		ListenableFuture<SendResult<Integer, String>> result = kafkaTemplate.send(KafkaTopics.TOPIC_BOOK, key, message);
		result.addCallback(new ListenableFutureCallback<SendResult<Integer, String>>() {
			@Override
			public void onSuccess(SendResult<Integer, String> result) {
				successCallBack(result);
			}

			@Override
			public void onFailure(Throwable ex) {
				failureCallBack(ex);
			}
		});
		log.info("sent kafka message");
	}

	private void failureCallBack(Throwable ex) {
		log.error(ex.getMessage());
	}

	private void successCallBack(SendResult<Integer, String> result) {
		log.info("Topic Name -" + result.getProducerRecord().topic());
		log.info("Partition Name -" + result.getProducerRecord().toString());
	}

	public void sendAddBookMessageAsync(BookRequestModel book, Map<String, String> headersMap)
			throws JsonProcessingException {
		Integer key=book.getBookId();
		String message = mapper.writeValueAsString(book);
		log.info("sending kafka message");
		// topic The topic the record will be appended to
		// Partition The partition to which the record should be sent
		// key The key that will be included in the record
		// value The record contents
		// headers The headers that will be included in the record
		List<Header> headers=getHeaders(headersMap);
		kafkaTemplate.send(new ProducerRecord<Integer, String>(KafkaTopics.TOPIC_BOOK, null, key, message, headers));
	}

	private List<Header> getHeaders(Map<String, String> headersMap) {
		List<Header> headers = new ArrayList<>();
		headersMap.forEach((key, value) -> {
			RecordHeader rh = new RecordHeader(key, value.getBytes());
			headers.add(rh);
		});
		return headers;
	}
}
