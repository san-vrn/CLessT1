package org.example.kafka;

import lombok.RequiredArgsConstructor;
import org.example.dto.TaskDto;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.KafkaHeaders;
import org.springframework.messaging.Message;
import org.springframework.messaging.support.MessageBuilder;
import org.springframework.stereotype.Component;

import java.util.UUID;

@RequiredArgsConstructor
@Component
public class KafkaTaskProducer {

    @Value("${app.kafka.task_topic}")
    private String topicName;
    private final KafkaTemplate<String, TaskDto> kafkaTemplate;

    public void send(TaskDto taskDto){
        Message<TaskDto> message = MessageBuilder
                .withPayload(taskDto)
                .setHeader(KafkaHeaders.KEY, UUID.randomUUID().toString())
                .setHeader(KafkaHeaders.TOPIC, topicName)
                .build();

     kafkaTemplate.send(message);
    }
}
