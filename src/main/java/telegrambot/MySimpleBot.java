package telegrambot;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URI;
import java.net.URL;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Location;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.User;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

public class MySimpleBot extends TelegramLongPollingBot {

	private static final Logger logger = LoggerFactory.getLogger(MySimpleBot.class);
	
    private final String BOT_USERNAME;
    private final String BOT_TOKEN;
   
	public MySimpleBot(String botToken, String userName) {
		super(botToken);
		BOT_USERNAME=userName;
		BOT_TOKEN=botToken;
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
        }else if (update.hasMessage() && update.getMessage().hasLocation()) {
            Location userLocation = update.getMessage().getLocation();
            String chatId = update.getMessage().getChatId().toString();
            
            String coordinates = Utils.coordinateToGeojsonPoint(userLocation.getLatitude(), userLocation.getLongitude()); 
                                  
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
            	message.setText("Hola " +firstName+", me acabas de mandar una ubicacion: "+ coordinates);
            }else {
            	message.setText("Hello " +firstName+", you just messaged me an ubication: "+ coordinates);	
            }
            
            try {
                execute(message);
            } catch (TelegramApiException e) {
                logger.error("Error sending message", e);
            }
            
            File mapHtmlFile = Utils.createMapHtmlFile(userLocation.getLatitude(), userLocation.getLongitude());
            String filePath="";
            try {
				filePath=Utils.generateImageFromHtml(mapHtmlFile);
			} catch (Exception e) {
				logger.error("Error generating image from html file", e);
			}
            
            File file = new File(filePath);

            try {
				sendPhoto(BOT_TOKEN, chatId, file);
			} catch (IOException e) {
				logger.error("Error sending image", e);
			}
        }
    }

    //TODO big refactor here
    public static void sendPhoto(String botToken, String chatId, File photoFile) throws IOException {
        String urlString = "https://api.telegram.org/bot" + botToken + "/sendPhoto";

        // Conexión HTTP POST multipart/form-data
        String boundary = "----WebKitFormBoundary" + System.currentTimeMillis();
        URL url = URI.create(urlString).toURL();
        HttpURLConnection conn = (HttpURLConnection) url.openConnection();
        conn.setDoOutput(true);
        conn.setRequestMethod("POST");
        conn.setRequestProperty("Content-Type", "multipart/form-data; boundary=" + boundary);

        try (DataOutputStream out = new DataOutputStream(conn.getOutputStream())) {
            // Campo chat_id
            out.writeBytes("--" + boundary + "\r\n");
            out.writeBytes("Content-Disposition: form-data; name=\"chat_id\"\r\n\r\n");
            out.writeBytes(chatId + "\r\n");

            // Campo photo (archivo)
            out.writeBytes("--" + boundary + "\r\n");
            out.writeBytes("Content-Disposition: form-data; name=\"photo\"; filename=\"" + photoFile.getName() + "\"\r\n");
            out.writeBytes("Content-Type: image/png\r\n\r\n");

            try (FileInputStream fileInputStream = new FileInputStream(photoFile)) {
                fileInputStream.transferTo(out);
            }
            out.writeBytes("\r\n--" + boundary + "--\r\n");
            out.flush();
        }

        // Leer respuesta
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(conn.getInputStream()))) {
            String line;
            while ((line = reader.readLine()) != null) {
                System.out.println(line);
            }
        }
        conn.disconnect();
    }
}
