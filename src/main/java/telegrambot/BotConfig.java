package telegrambot;

import java.io.IOException;
import java.io.InputStream;
import java.util.Arrays;
import java.util.List;
import java.util.Properties;

public class BotConfig {

    private String botUsername;
    private String botToken;
    private String supabaseAPI;
    private String supabaseURL;
    private List<String> dailyChatIds;

    public BotConfig() {
        Properties prop = new Properties();
        try (InputStream input = getClass().getClassLoader().getResourceAsStream("bot.properties")) {
            if (input == null) {
                throw new RuntimeException("File bot.properties not found");
            }
            prop.load(input);
            botUsername = prop.getProperty("bot.username");
            botToken = prop.getProperty("bot.token");
            supabaseAPI = prop.getProperty("bot.supabase.api");
            supabaseURL = prop.getProperty("bot.supabase.url");
            dailyChatIds = Arrays.stream(prop.getProperty("bot.daily.chat.ids", "").split(","))
                    .map(String::trim)
                    .filter(chatId -> !chatId.isEmpty())
                    .toList();
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

	public String getSupabaseAPI() {
		return supabaseAPI;
	}

	public String getSupabaseURL() {
		return supabaseURL;
	}

    public List<String> getDailyChatIds() {
        return dailyChatIds;
    }

	@Override
	public String toString() {
		return "BotConfig [botUsername=" + botUsername + ", botToken=" + botToken + "]";
	}
    
    
}

