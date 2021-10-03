package com.project.kafka.producer.controller;

import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.project.kafka.producer.ResquestBodyModel.BookRequestModel;
import com.project.kafka.producer.eventHandler.KafkaBookEventProducer;

import lombok.extern.slf4j.Slf4j;

@RestController
@RequestMapping(path = "/v1/publishMessage")
@Slf4j

public class EventProducerController {

	@Autowired
	KafkaBookEventProducer bookEventProducer;

	@PostMapping("/sync/addBook")
	// Synchronous message sending :no separate thread for sending message and no
	// call back
	public void addBookSynchronously(@RequestBody BookRequestModel book) throws JsonProcessingException {
		log.info("addBook" + book.toString());
		bookEventProducer.sendAddBookMessageSync(book);
		log.info("book added");
	}

	@PostMapping("/async/addBook")
	// ASynchronous message sending : separate thread for sending message and no
	// callback
	public void addBookAsync(@RequestBody BookRequestModel book) throws JsonProcessingException {
		log.info("addBook" + book.toString());
		bookEventProducer.sendAddBookMessageAsync(book);
		log.info("book added");
	}

	@PostMapping("/async-headers/addBook")
	// ASynchronous message sending : separate thread for sending message and no
	// call back
	public void addBookAsync_WithHeaders(@RequestBody BookRequestModel book, @RequestHeader Map<String, String> headers)
			throws JsonProcessingException {
		log.info("Headers details-->" + headers);
		bookEventProducer.sendAddBookMessageAsync(book,headers);
		log.info("book added");
	}
}
