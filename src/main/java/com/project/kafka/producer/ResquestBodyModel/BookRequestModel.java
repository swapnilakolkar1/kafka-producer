package com.project.kafka.producer.ResquestBodyModel;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class BookRequestModel {
	Integer bookId;
	String bookName;
	String author;

}
