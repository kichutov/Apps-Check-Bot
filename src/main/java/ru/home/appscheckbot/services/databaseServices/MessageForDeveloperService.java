package ru.home.appscheckbot.services.databaseServices;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Example;
import org.springframework.data.domain.ExampleMatcher;
import org.springframework.stereotype.Service;
import ru.home.appscheckbot.models.MessageForDeveloper;
import ru.home.appscheckbot.repository.MessageForDeveloperRepository;

import java.util.List;

import static org.springframework.data.domain.ExampleMatcher.GenericPropertyMatchers.exact;

@Service
public class MessageForDeveloperService {

    private final MessageForDeveloperRepository messageForDeveloperRepository;

    @Autowired
    public MessageForDeveloperService(MessageForDeveloperRepository messageForDeveloperRepository) {
        this.messageForDeveloperRepository = messageForDeveloperRepository;
    }

    public MessageForDeveloper saveMessageForDeveloper(MessageForDeveloper messageForDeveloper) {
        return messageForDeveloperRepository.save(messageForDeveloper);
    }

    public List<MessageForDeveloper> findAlMessageForDeveloperByUserId(int userId) {
        MessageForDeveloper messageForDeveloper = new MessageForDeveloper();
        messageForDeveloper.setUserId(userId);
        ExampleMatcher matcher = ExampleMatcher.matching().withMatcher("userId", exact());
        Example example = Example.of(messageForDeveloper, matcher);
        return messageForDeveloperRepository.findAll(example);
    }
}
