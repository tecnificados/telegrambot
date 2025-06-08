package telegrambot;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

public class MySimpleBot extends TelegramLongPollingBot {

	private static final Logger logger = LoggerFactory.getLogger(MySimpleBot.class);
	
    private final String BOT_USERNAME = "tecnificados_bot";
   
	public MySimpleBot(String botToken) {
		super(botToken);
	}

	@Override
    public String getBotUsername() {
        return BOT_USERNAME;
    }
   
    @Override
    public void onUpdateReceived(Update update) {
        if (update.hasMessage() && update.getMessage().hasText()) {
            String userMessage = update.getMessage().getText();
            String chatId = update.getMessage().getChatId().toString();

            SendMessage message = new SendMessage();
            message.setChatId(chatId);
            message.setText("Echo: " + userMessage);

            try {
                execute(message);
            } catch (TelegramApiException e) {
                logger.error("Error sending message", e);
            }
        }
    }
}
