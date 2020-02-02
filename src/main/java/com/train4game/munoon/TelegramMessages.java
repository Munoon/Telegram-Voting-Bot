package com.train4game.munoon;

import lombok.AllArgsConstructor;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.io.IOException;
import java.io.InputStream;
import java.text.MessageFormat;
import java.util.Properties;
import java.util.StringJoiner;

@Component
public class TelegramMessages {
    private Properties properties;

    @PostConstruct
    public void loadProperties() throws IOException {
        ClassPathResource resource = new ClassPathResource("messages.xml");
        InputStream inputStream = resource.getInputStream();

        properties = new Properties();
        properties.loadFromXML(inputStream);
    }

    public String getProperty(String key) {
        String message = properties.getProperty(key);

        if (message == null) {
            return null;
        }

        StringJoiner joiner = new StringJoiner("\n");
        for (String line : message.strip().split("\n")) {
            joiner.add(line.strip());
        }

        return joiner.toString();
    }

    public String getProperty(String key, Object... arguments) {
        String message = getProperty(key);
        return MessageFormat.format(message, arguments);
    }

    public TelegramMessages createWrapper(String prefix) {
        return new Wrapper(this, prefix);
    }

    @AllArgsConstructor
    private class Wrapper extends TelegramMessages {
        private TelegramMessages telegramMessages;
        private String prefix;

        @Override
        public String getProperty(String key) {
            return telegramMessages.getProperty(prefix + "." + key);
        }
    }
}
