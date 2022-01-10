package ru.home.appscheckbot.DAO;

import org.springframework.stereotype.Service;
import ru.home.appscheckbot.models.MessageForDeveloper;
import ru.home.appscheckbot.repository.MessageForDeveloperRepository;

@Service
public class MessageForDeveloperDAO {

    private final MessageForDeveloperRepository messageForDeveloperRepository;

    public MessageForDeveloperDAO(MessageForDeveloperRepository messageForDeveloperRepository) {
        this.messageForDeveloperRepository = messageForDeveloperRepository;
    }


    public void saveMessageForDeveloper(MessageForDeveloper messageForDeveloper) {
        messageForDeveloperRepository.save(messageForDeveloper);
    }
}
