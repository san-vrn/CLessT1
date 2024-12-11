package org.example.service;

import lombok.AllArgsConstructor;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.example.dto.TaskDto;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

@AllArgsConstructor
@Service
public class NotificationService {

    private final EmailService emailService;
    private static final Logger logger = LoggerFactory.getLogger(NotificationService.class);

    public void processMessage(ConsumerRecord<String, TaskDto> consumerRecord) {
        TaskDto message = consumerRecord.value();
        emailService.sendSimpleMessage("Задача обновилась, новый статус задачи: " + message.getStatus()
                , "Задача " + message.getTitle()+ " обновлена");
        logger.info("новое сообщение в топике:{}", message);
    }
}
