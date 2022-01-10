package ru.home.appscheckbot.services;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.objects.Message;


@Service
public class MessageForDeveloperService {

    private final TextService textService;
    private final SendMessageService sendMessageService;
    @Value("${adminId}")
    String adminId;

    public MessageForDeveloperService(TextService textService,
                                      SendMessageService sendMessageService) {
        this.textService = textService;
        this.sendMessageService = sendMessageService;
    }


    public void sendMessageForDeveloper(Message message) {
        String messageForDeveloper = textService.getText("admin.messageForDeveloper",
                message.getFrom().getUserName(),
                message.getFrom().getId().toString(),
                message.getText());
        sendMessageService.SendMessage(adminId, messageForDeveloper);
    }
}
