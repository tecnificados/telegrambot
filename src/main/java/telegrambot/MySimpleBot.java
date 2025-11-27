package telegrambot;

import java.awt.image.BufferedImage;
import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URI;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import javax.imageio.ImageIO;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.methods.send.SendPhoto;
import org.telegram.telegrambots.meta.api.methods.updatingmessages.EditMessageReplyMarkup;
import org.telegram.telegrambots.meta.api.objects.InputFile;
import org.telegram.telegrambots.meta.api.objects.Location;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.User;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import telegrambot.bean.IbexBean;
import telegrambot.cards.Carta;
import telegrambot.cards.SpriteDeckLoader;

public class MySimpleBot extends TelegramLongPollingBot {

	private static final Logger logger = LoggerFactory.getLogger(MySimpleBot.class);
	
    private final String BOT_USERNAME;
    private final String BOT_TOKEN;
    
    List<Carta> mazo = new ArrayList<Carta>();
   
	public MySimpleBot(String botToken, String userName) {
		super(botToken);
		BOT_USERNAME=userName;
		BOT_TOKEN=botToken;
		
		try {
            mazo = SpriteDeckLoader.loadDeck();           
        } catch (IOException e) {
            logger.error("Error loading cards", e);
        }
		
		
	}

	@Override
    public String getBotUsername() {
        return BOT_USERNAME;
    }
   
    @Override
    public void onUpdateReceived(Update update) {
    	 
    	//Contestación por botones
    	if (update.hasCallbackQuery()) {
    		Long chatId = update.getCallbackQuery().getMessage().getChatId();
            String data = update.getCallbackQuery().getData();
            Integer messageId = update.getCallbackQuery().getMessage().getMessageId();
            
            SendMessage message = new SendMessage();
            message.setChatId(chatId);
            if (data.equals("COND_SI")) { 
 	            message.setText("Has elegido si");
            }else  { 
 	            message.setText("Has elegido no");
            }
            sendMessage(message);
            
         // 2️⃣ Deshabilitar los botones
            EditMessageReplyMarkup editMarkup = new EditMessageReplyMarkup();
            editMarkup.setChatId(chatId.toString());
            editMarkup.setMessageId(messageId);
            editMarkup.setReplyMarkup(null);  // borra los botones

            try {
                execute(editMarkup);
            } catch (TelegramApiException e) {
                e.printStackTrace();
            }
        }
    	else if (update.hasMessage() && update.getMessage().hasText()) {
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
            
            String userMessageCleaned=userMessage.toLowerCase().trim();            
            
            //TODO another refactor ASAP
            if (languageCode!=null && languageCode.equals("es") && (userMessageCleaned.equals("carta") || userMessageCleaned.equals("card"))) {
            	Carta carta = mazo.get(Utils.randomNumber(0, mazo.size()));
            	//TODO extraer la siguiente linea a una función de la carta en multidioma
            	String nombreCarta=carta.getValor()+" de "+carta.getPalo();
	            try
	            {	            	
	            	ByteArrayOutputStream baos = new ByteArrayOutputStream();
	                ImageIO.write(carta.getImagen(), "png", baos);
	                baos.flush();
	
	                // Crear InputStream para Telegram
	                ByteArrayInputStream inputStream = new ByteArrayInputStream(baos.toByteArray());
	                InputFile inputFile = new InputFile(inputStream, carta.getValor()+"_"+carta.getPalo()  + ".png");
	                
	                SendPhoto sendPhoto = new SendPhoto();
	                sendPhoto.setChatId(chatId.toString());
	                sendPhoto.setPhoto(inputFile);
	                sendPhoto.setCaption(nombreCarta);
	
	                execute(sendPhoto);
	
	                baos.close();
	                inputStream.close();
	            } catch (IOException | TelegramApiException e) {
	                logger.error("Error sending card", e);
	            }            	
            } else if (languageCode!=null && languageCode.equals("es") && (userMessageCleaned.equals("mano") || userMessageCleaned.equals("hand"))) {
            	List<Carta> myDeck = new ArrayList<Carta>();
            	myDeck.addAll(mazo);
            	Collections.shuffle(myDeck);
            	List<Carta> pokerHand = myDeck.subList(0, 5);
            	
            	List<BufferedImage> images = pokerHand.stream().map(c -> c.getImagen()).toList();
                BufferedImage combined = Utils.combineCardsOverlapping(images);            	
        
                
                //Combinar los nombres de cada carta
            	String nombreCarta="Una mano de poker";
	            try
	            {	     
	            	
	            	ByteArrayOutputStream baos = new ByteArrayOutputStream();
	                ImageIO.write(combined, "png", baos);
	                baos.flush();
	
	                // Crear InputStream para Telegram
	                ByteArrayInputStream inputStream = new ByteArrayInputStream(baos.toByteArray());
	                InputFile inputFile = new InputFile(inputStream, "pokerHand"  + ".png");
	                
	                SendPhoto sendPhoto = new SendPhoto();
	                sendPhoto.setChatId(chatId.toString());
	                sendPhoto.setPhoto(inputFile);
	                sendPhoto.setCaption(nombreCarta);
	
	                execute(sendPhoto);
	
	                baos.close();
	                inputStream.close();
	            } catch (IOException | TelegramApiException e) {
	                logger.error("Error sending card", e);
	            } 
            } else if (languageCode!=null && languageCode.equals("es") && (userMessageCleaned.equals("ibex") || userMessageCleaned.equals("ibex 35"))) {
            	String data = SupabaseRead.readIbexData();            	
            	Map<Character, List<IbexBean>> grupos = Utils.formatIbexData(data);                
                for (Map.Entry<Character, List<IbexBean>> entry : grupos.entrySet()) {                	
        			message.setParseMode("HTML");        			
        			String output=""; 
        			output+="<b><u>"+entry.getKey()+"</u></b>"+"\n"+"\n";
        			for (IbexBean bean : entry.getValue()) {
        				output+=bean.output()+"\n";
        		    }
        			sendMessage(message, output);
        		}
            } else if (languageCode!=null && languageCode.equals("es") && (userMessageCleaned.equals("prueba") || userMessageCleaned.equals("test"))) {
            	SendMessage msg = new SendMessage();
            	msg.setChatId(chatId);
            	msg.setText("¿Qué opinas?");

            	InlineKeyboardMarkup markup = new InlineKeyboardMarkup();

            	List<List<InlineKeyboardButton>> rows = new ArrayList<>();

            	List<InlineKeyboardButton> row = new ArrayList<>();
            	row.add(InlineKeyboardButton.builder()
            	        .text("Sí, condensación ❄")
            	        .callbackData("COND_SI")
            	        .build());

            	row.add(InlineKeyboardButton.builder()
            	        .text("No hay condensación")
            	        .callbackData("COND_NO")
            	        .build());

            	rows.add(row);
            	markup.setKeyboard(rows);

            	msg.setReplyMarkup(markup);
            	sendMessage(msg);

	        } else {
	            if (languageCode!=null && languageCode.equals("es")) {
	            	message.setText("Hola " +firstName+", me acabas de escribir: "+ userMessage);
	            }else {
	            	message.setText("Hello " +firstName+", you just messaged me this: "+ userMessage);	
	            }
	            sendMessage(message);
            }           
        }else if (update.hasMessage() && update.getMessage().hasLocation()) {
        	String chatId = update.getMessage().getChatId().toString(); 
            Location userLocation = update.getMessage().getLocation();
            
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

	
    private void sendMessage(SendMessage message) {
		try {
		    execute(message);
		} catch (TelegramApiException e) {
		    logger.error("Error sending message", e);
		}
	}
	

	private void sendMessage(SendMessage message, String output) {
		message.setText(output);
		try {
		    execute(message);
		} catch (TelegramApiException e) {
		    logger.error("Error sending message", e);
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
