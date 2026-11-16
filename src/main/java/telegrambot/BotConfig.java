package telegrambot;

import java.io.IOException;
import java.io.Reader;
import java.net.URISyntaxException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
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
        try {
            Path codeLocation = Path.of(BotConfig.class.getProtectionDomain()
                    .getCodeSource().getLocation().toURI());
            Path configPath = Files.isDirectory(codeLocation)
                    ? codeLocation.resolve("bot.properties")
                    : codeLocation.resolveSibling("bot.properties");

            if (!Files.isRegularFile(configPath)) {
                throw new IllegalStateException("No se encontró bot.properties junto al programa: " + configPath);
            }

            try (Reader reader = Files.newBufferedReader(configPath, StandardCharsets.UTF_8)) {
                prop.load(reader);
            }
            botUsername = prop.getProperty("bot.username");
            botToken = prop.getProperty("bot.token");
            supabaseAPI = prop.getProperty("bot.supabase.api");
            supabaseURL = prop.getProperty("bot.supabase.url");
            dailyChatIds = Arrays.stream(prop.getProperty("bot.daily.chat.ids", "").split(","))
                    .map(String::trim)
                    .filter(chatId -> !chatId.isEmpty())
                    .toList();
        } catch (IOException | URISyntaxException e) {
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
		return "BotConfig [botUsername=" + botUsername + "]";
	}
    
    
}

