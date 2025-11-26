package telegrambot;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class SupabaseRead {
	
	private static final Logger logger = LoggerFactory.getLogger(SupabaseRead.class);
	
	public static String readData() {
		String out="";
		BotConfig botConfig = new BotConfig();
    	
        String supabaseUrl = botConfig.getSupabaseURL();
        String apiKey = botConfig.getSupabaseAPI();
        String endpoint = supabaseUrl + "/rest/v1/ibexdata?select=name,value,datet&order=datet.desc";

        HttpClient client = HttpClient.newHttpClient();
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(endpoint))
                .header("apikey", apiKey)
                .header("Authorization", "Bearer " + apiKey)
                .GET()
                .build();

        HttpResponse<String> response;
		try {
			response = client.send(request, HttpResponse.BodyHandlers.ofString());
			out=response.body();
			logger.info("Status: " + response.statusCode());
		    logger.info("Response: " + response.body());
		} catch (Exception e) {
			logger.error("Error reading data",e);
		} 
        return out;
	}
	
    public static void main(String[] args) throws Exception {
    	String data=readData();
    	logger.info(data);
    }
}
