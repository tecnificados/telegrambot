package telegrambot;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

public class BotConfig {

    private String botUsername;
    private String botToken;

    public BotConfig() {
        Properties prop = new Properties();
        try (InputStream input = getClass().getClassLoader().getResourceAsStream("bot.properties")) {
            if (input == null) {
                throw new RuntimeException("File bot.properties not found");
            }
            prop.load(input);
            botUsername = prop.getProperty("bot.username");
            botToken = prop.getProperty("bot.token");
        } catch (IOException e) {
            throw new RuntimeException("Error cargando propiedades del bot", e);
        }
    }

    public String getBotUsername() {
        return botUsername;
    }

    public String getBotToken() {
        return botToken;
    }
}

