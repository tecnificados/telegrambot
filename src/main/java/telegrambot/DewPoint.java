package telegrambot;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import com.google.gson.Gson;
import telegrambot.model.openmeteo.WeatherData;

public class DewPoint {
	
	private static final Logger logger = LoggerFactory.getLogger(DewPoint.class);

	public static void main(String[] args)  {
		double lat = 40.4168;
		double lon = -3.7038;

		List<String> dewPointConclusions = new ArrayList<String>();
		try {
			dewPointConclusions = dewPointConclusions(lat, lon);
		} catch (IOException | InterruptedException e) {
			logger.error("Error on dewPoints", e);
			e.printStackTrace();
		} 
		
		for (String actual:dewPointConclusions) {
			logger.info(actual);
		}
	}

	private static List<String> dewPointConclusions(double lat, double lon) throws IOException, InterruptedException {
		List<String> conclusions = new ArrayList<String>();
		String url = "https://api.open-meteo.com/v1/forecast?latitude=" + lat + "&longitude=" + lon
				+ "&daily=temperature_2m_max,temperature_2m_min,dew_point_2m_max,dew_point_2m_min"
				+ "&timezone=Europe/Madrid";

		HttpClient client = HttpClient.newHttpClient();
		HttpRequest request = HttpRequest.newBuilder().uri(URI.create(url)).build();

		HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

		Gson gson = new Gson();
		WeatherData data = gson.fromJson(response.body(), WeatherData.class);

		// Mostrar resultados
		for (int i = 0; i < data.getDaily().getTime().length; i++) {
			String fecha = data.getDaily().getTime()[i];
			double tmax = data.getDaily().getTemperatureMax()[i];
			double tmin = data.getDaily().getTemperatureMin()[i];
			double dewMax = data.getDaily().getDewPointMax()[i];
			// double dewMin = data.getDaily().getDewPointMin()[i];

			boolean condensacion = tmin <= dewMax;

			Locale localeEs = Locale.forLanguageTag("es-ES");

			String actualConclusion = "Día " + fecha + " -> Tmax=" + String.format(localeEs, "%.1f", tmax) + "°C, "
					+ "Tmin=" + String.format(localeEs, "%.1f", tmin) + "°C, " + "Punto de rocío="
					+ String.format(localeEs, "%.1f", dewMax) + "°C → "
					+ (condensacion ? "❄ Condensación probable" : "No hay condensación");

			conclusions.add(actualConclusion);

		}

		return conclusions;
	}

}
