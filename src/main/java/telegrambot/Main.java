package telegrambot;

import java.io.IOException;
import java.time.Duration;
import java.time.LocalDateTime;
import java.time.LocalTime;
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
			
			scheduleDailyMessage(mySimpleBot, "1329746834");
		} catch (Exception e) {
			logger.error("Error running bot", e);
		}
	}
	
	private static long getDelayUntilNextExecution(LocalTime targetTime) {
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime nextRun = now.withHour(targetTime.getHour())
                                   .withMinute(targetTime.getMinute())
                                   .withSecond(0)
                                   .withNano(0);

        if (now.isAfter(nextRun)) {
            nextRun = nextRun.plusDays(1);
        }

        Duration duration = Duration.between(now, nextRun);
        return duration.getSeconds();
    }
    
    // 🕒 Programa el envío diario a las 12:00
    public static void scheduleDailyMessage(MySimpleBot bot, String chatId) {
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
        	
            try {
                SendMessage message = new SendMessage();
                message.setChatId(chatId);
                
                if (dewPointConclusions==null ||dewPointConclusions.size()==0) {
                	message.setText("🌞 ¡Hola! Este es tu mensaje diario de las 12:00.");
                } else {
                	String finalMessage="🌞 ¡Hola! Este es tu mensaje diario de las 12:00. \n\n";
                	for (String actual:dewPointConclusions) {
                		finalMessage+=actual+"\n";
                	}
                	message.setText(finalMessage);
                }               
                              
                bot.execute(message);
                logger.info("Mensaje enviado a las 12:00");
            } catch (TelegramApiException e) {
                logger.error("Error sending message in scheduler", e);
            }
        };

        // Calcular tiempo inicial hasta las 12:00 de hoy o mañana
        long initialDelay = getDelayUntilNextExecution(LocalTime.of(12, 00));
        long period = TimeUnit.DAYS.toSeconds(1);

        scheduler.scheduleAtFixedRate(task, initialDelay, period, TimeUnit.SECONDS);
    }
}
