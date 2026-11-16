package telegrambot;

import java.io.IOException;
import java.time.LocalTime;
import java.time.ZoneId;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.telegram.telegrambots.meta.TelegramBotsApi;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import org.telegram.telegrambots.updatesreceivers.DefaultBotSession;
import java.util.ArrayList;
import java.util.List;

public class Main {
	
	private static final Logger logger = LoggerFactory.getLogger(Main.class);
	
	public static void main(String[] args) {
		try {
			BotConfig config = new BotConfig();
			logger.info(config.toString());
			TelegramBotsApi botsApi = new TelegramBotsApi(DefaultBotSession.class);
			MySimpleBot mySimpleBot = new MySimpleBot(config.getBotToken(), config.getBotUsername());
			botsApi.registerBot(mySimpleBot);
			
			scheduleDailyMessage(mySimpleBot, config.getDailyChatIds());
		} catch (Exception e) {
			logger.error("Error running bot", e);
		}
	}
	
	
    
    // 🕒 Programa el envío diario a las 12:00
    public static void scheduleDailyMessage(MySimpleBot bot, List<String> chatIds) {
        if (chatIds == null || chatIds.isEmpty()) {
            logger.warn("No hay identificadores configurados para el mensaje meteorológico diario");
            return;
        }

        ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(1);                
        Runnable task = () -> {
        	
        	double lat = 40.4168;
    		double lon = -3.7038;
    		List<String> dewPointConclusions = new ArrayList<String>();		
    		try {
    			dewPointConclusions = DewPoint.dewPointConclusions(lat, lon);
    		} catch (IOException | InterruptedException e) {
    			logger.error("Error on dewPoints", e);
    		} 
        	
            String finalMessage;
            if (dewPointConclusions == null || dewPointConclusions.isEmpty()) {
                finalMessage = "🌞 ¡Hola! Este es tu mensaje diario de las 12:00.";
            } else {
                StringBuilder messageBuilder = new StringBuilder("🌞 ¡Hola! Este es tu mensaje diario de las 12:00.\n\n");
                for (String conclusion : dewPointConclusions) {
                    messageBuilder.append(conclusion).append('\n');
                }
                finalMessage = messageBuilder.toString();
            }

            for (String chatId : chatIds) {
                try {
                    SendMessage message = new SendMessage();
                    message.setChatId(chatId);
                    message.setText(finalMessage);
                    bot.execute(message);
                    logger.info("Mensaje meteorológico diario enviado");
                } catch (TelegramApiException e) {
                    logger.error("Error enviando mensaje meteorológico diario", e);
                }
            }
        };

        // Calcular tiempo inicial hasta las 12:00 de hoy o mañana
        ZoneId zone = ZoneId.of("Europe/Madrid");
        long initialDelay = Utils.getDelayUntilNextExecution(LocalTime.of(12, 00), zone);
        long period = TimeUnit.DAYS.toSeconds(1);

        scheduler.scheduleAtFixedRate(task, initialDelay, period, TimeUnit.SECONDS);
    }
}
