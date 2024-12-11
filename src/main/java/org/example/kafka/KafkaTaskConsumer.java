package org.example.kafka;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.example.dto.TaskDto;
import org.example.service.NotificationService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.support.Acknowledgment;
import org.springframework.stereotype.Component;

import java.util.List;

@Slf4j
@AllArgsConstructor
@Component
public class KafkaTaskConsumer {

    private final NotificationService notificationService;
    private final Logger logger = LoggerFactory.getLogger(KafkaTaskConsumer.class);

    @KafkaListener(topics = "${app.kafka.task_topic}")
    public void recieve(List<ConsumerRecord<String, TaskDto>> consumerRecords, Acknowledgment acknowledgment) {
        if (consumerRecords.isEmpty()) {
            logger.warn("Пустой consumerRecords: {}", consumerRecords);
            return;
        }
        for (ConsumerRecord<String, TaskDto> consumerRecord : consumerRecords) {
            if (consumerRecord.value() == null || consumerRecord.value().getTitle() == null) {
                logger.warn("Получено пустое сообщение в Kafka: {}", consumerRecord);
                return;
            }
            notificationService.processMessage(consumerRecord);
        }
        acknowledgment.acknowledge();
    }
}
