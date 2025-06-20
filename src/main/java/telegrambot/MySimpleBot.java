package telegrambot;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.User;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

public class MySimpleBot extends TelegramLongPollingBot {

	private static final Logger logger = LoggerFactory.getLogger(MySimpleBot.class);
	
    private final String BOT_USERNAME;
   
	public MySimpleBot(String botToken, String userName) {
		super(botToken);
		BOT_USERNAME=userName;
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
            
          
            User user = update.getMessage().getFrom();            
            //Long userId = user.getId();
            String firstName = user.getFirstName();
            //String lastName = user.getLastName();
            //String username = user.getUserName();
            String languageCode = user.getLanguageCode();
            logger.info("language: "+languageCode);

            SendMessage message = new SendMessage();
            message.setChatId(chatId);
            if (languageCode!=null && languageCode.equals("es")) {
            	message.setText("Hola " +firstName+", me acabas de escribir: "+ userMessage);
            }else {
            	message.setText("Hello " +firstName+", you just messaged me this: "+ userMessage);	
            }
            
            try {
                execute(message);
            } catch (TelegramApiException e) {
                logger.error("Error sending message", e);
            }
        }
    }
}
