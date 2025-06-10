package telegrambot;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.telegram.telegrambots.meta.TelegramBotsApi;
import org.telegram.telegrambots.updatesreceivers.DefaultBotSession;

public class Main {
	
	private static final Logger logger = LoggerFactory.getLogger(Main.class);
	
	public static void main(String[] args) {
		try {
			BotConfig config = new BotConfig();
			logger.info(config.toString());
			TelegramBotsApi botsApi = new TelegramBotsApi(DefaultBotSession.class);
			botsApi.registerBot(new MySimpleBot(config.getBotToken(), config.getBotUsername()));
		} catch (Exception e) {
			logger.error("Error running bot", e);
		}
	}
}
