package org.example.kafka;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.example.dto.TaskDto;
import org.example.service.NotificationService;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Slf4j
@RequiredArgsConstructor
@Component
public class KafkaTaskConsumer {

    private final NotificationService notificationService;

    @KafkaListener(topics = "${app.kafka.task_topic}")
    public void recieve(ConsumerRecord<String, TaskDto> consumerRecord) {
        notificationService.processMessage(consumerRecord);
    }
}
