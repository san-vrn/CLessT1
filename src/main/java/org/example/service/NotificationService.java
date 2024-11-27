package org.example.service;

import lombok.RequiredArgsConstructor;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.example.dto.TaskDto;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

@RequiredArgsConstructor
@Service
public class NotificationService {

    private final EmailService emailService;
    private static final Logger logger = LoggerFactory.getLogger(NotificationService.class);

    public void processMessage(ConsumerRecord<String, TaskDto> consumerRecord) {
        if(consumerRecord.value()==null || consumerRecord.value().getTitle()==null ){
            logger.warn("Получено пустое сообщение в Kafka: {}", consumerRecord);
            return;
        }
        TaskDto message = consumerRecord.value();
        emailService.sendSimpleMessage("Задча обновилась, новый статус задачи: " + message.getStatus());
        logger.info("новое сообщение в топике:{}", message);
    }
}
