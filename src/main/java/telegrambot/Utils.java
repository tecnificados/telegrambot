package telegrambot;

import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileWriter;
import java.nio.file.Files;
import java.time.Duration;
import java.time.LocalTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Random;
import java.util.TreeMap;
import java.util.stream.Collectors;

import org.openqa.selenium.By;
import org.openqa.selenium.OutputType;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import telegrambot.bean.IbexBean;

public class Utils {
	
	//TODO extract selenium util to another class
	
	private static final Logger logger = LoggerFactory.getLogger(Utils.class);
	
	public static Random random = new Random();	
		
	public static BufferedImage combineCards(List<BufferedImage> cards) {
	    int cardWidth = cards.get(0).getWidth();
	    int cardHeight = cards.get(0).getHeight();
	    int spacing = 10; // espacio entre cartas

	    int totalWidth = cardWidth * cards.size() + spacing * (cards.size() - 1);
	    BufferedImage combined = new BufferedImage(totalWidth, cardHeight, BufferedImage.TYPE_INT_ARGB);
	    Graphics2D g2d = combined.createGraphics();

	    for (int i = 0; i < cards.size(); i++) {
	        int x = i * (cardWidth + spacing);
	        g2d.drawImage(cards.get(i), x, 0, null);
	    }

	    g2d.dispose();
	    return combined;
	}
	
	public static BufferedImage combineCardsOverlapping(List<BufferedImage> cards) {
	    int cardWidth = cards.get(0).getWidth();
	    int cardHeight = cards.get(0).getHeight();
	    int overlap = cardWidth / 2; // mitad de la carta se solapa

	    int totalWidth = cardWidth + overlap * (cards.size() - 1);
	    BufferedImage combined = new BufferedImage(totalWidth, cardHeight, BufferedImage.TYPE_INT_ARGB);
	    Graphics2D g2d = combined.createGraphics();

	    for (int i = 0; i < cards.size(); i++) {
	        int x = i * overlap;
	        g2d.drawImage(cards.get(i), x, 0, null);
	    }

	    g2d.dispose();
	    return combined;
	}
	
	public static int randomNumber(int min, int max) {
	    if (min > max) {
	        return random.nextInt((min - max) + 1) + min;
	    }	   
	    return random.nextInt((max - min) + 1) + min;
	}

	public static String coordinateToGeojsonPoint(double lat, double lon)
	{
        Map<String, Object> geojson = new LinkedHashMap<>();
        geojson.put("type", "Point");
        geojson.put("coordinates", Arrays.asList(lon, lat));

        ObjectMapper mapper = new ObjectMapper();
        String json = "";
		try {
			json = mapper.writeValueAsString(geojson);
		} catch (JsonProcessingException e) {
			logger.error("Error generating point in geojson", e);
		}
        return json;
	}	

	public static File createMapHtmlFile(double lat, double lon) {
	    String htmlContent = String.format(Locale.US, """ 
	        <!DOCTYPE html>
	        <html>
	          <head>
	            <meta charset="utf-8" />
	            <title>Mapa con Leaflet</title>
	            <link
	              rel="stylesheet"
	              href="https://unpkg.com/leaflet@1.9.4/dist/leaflet.css"
	            />
	            <script src="https://unpkg.com/leaflet@1.9.4/dist/leaflet.js"></script>
	            <style>
	              #map { height: 400px; width: 600px; }
	            </style>
	          </head>
	          <body>
	            <div id="map"></div>
	            <script>
	              var map = L.map('map').setView([%f, %f], 18);
	              L.tileLayer('https://{s}.tile.openstreetmap.org/{z}/{x}/{y}.png', {
	                maxZoom: 19,
	                attribution: '&copy; OpenStreetMap contributors'
	              }).addTo(map);
	              var point = { "type": "Point", "coordinates": [%f, %f] };
	              L.geoJSON(point, {
	                pointToLayer: function (feature, latlng) {
	                  return L.marker(latlng).bindPopup("Ubicación");
	                }
	              }).addTo(map);
	            </script>
	          </body>
	        </html>
	        """, lat, lon, lon, lat);

	    File file = null;
	    try {
	        file = File.createTempFile("map_", ".html");
	        try (FileWriter writer = new FileWriter(file)) {
	            writer.write(htmlContent);
	        }
	    } catch (Exception e) {
	        logger.error("Error generating point in geojson", e);
	    }
	    return file;
	}
	
    public static String generateImageFromHtml(File htmlFile) throws Exception {
    	if (htmlFile==null) {
    		return "";
    	}
        // Configurar Selenium en modo headless
    	String userDataDir = Files.createTempDirectory("chrome-user-data").toAbsolutePath().toString();

        ChromeOptions options = new ChromeOptions();
        options.addArguments("--headless=new");
        options.addArguments("--window-size=600,400");
        options.addArguments("--no-sandbox");
        options.addArguments("--disable-dev-shm-usage");
        options.addArguments("--user-data-dir=" + userDataDir); // <--- clave
        
        
        
        
        
        WebDriver driver = new ChromeDriver(options);

        // Abrir el HTML localmente
        driver.get(htmlFile.toURI().toString());

        // Esperar a que Leaflet cargue los tiles (1-2 segundos suele bastar)
        Thread.sleep(2000);

        // Seleccionar el div del mapa
        WebElement mapDiv = driver.findElement(By.cssSelector("body > div#map"));

        // Capturar solo el mapa
        File screenshot = mapDiv.getScreenshotAs(OutputType.FILE);

        // Guardar la imagen
        File output = File.createTempFile("map_screenshot", "png");
        Files.copy(screenshot.toPath(), output.toPath(), java.nio.file.StandardCopyOption.REPLACE_EXISTING);

        logger.info("Image generated: " + output.getAbsolutePath());

        driver.quit();
        
        return output.getAbsolutePath();
    }
    
    static Map<Character, List<IbexBean>> formatIbexData(String data) {
		Map<String, IbexBean> latest = new HashMap<String, IbexBean>();
		ObjectMapper mapper = new ObjectMapper();
		IbexBean[] arr;
		try {
			arr = mapper.readValue(data, IbexBean[].class);
			// Map<name, IbexData> con el más reciente
		    latest = Arrays.stream(arr)
		    	    .collect(Collectors.toMap(
		    	        (IbexBean d) -> d.getName(),   // clave
		    	        (IbexBean d) -> d,             // valor
		    	        (d1, d2) -> d1.getDate().compareTo(d2.getDate()) > 0 ? d1 : d2 // merge function
		    	    ));
		} catch (Exception e) {
			logger.error("Error reading json", e);
		} 

		// Lista final de datos más recientes
		List<IbexBean> result = new ArrayList<>(latest.values());
		
		// Ordenar alfabéticamente por nombre
		result.sort(Comparator.comparing(IbexBean::getName));
		
		// Agrupar por primera letra
		Map<Character, List<IbexBean>> grupos = result.stream()
		        .collect(Collectors.groupingBy(
		                d -> d.getName().charAt(0),      // clave: primera letra
		                TreeMap::new,                    // para que las claves estén ordenadas
		                Collectors.toList()              // valor: lista de IbexBean
		));
		return grupos;
	}
    
    public static long getDelayUntilNextExecution(LocalTime targetTime, ZoneId zone) {
	    ZonedDateTime now = ZonedDateTime.now(zone);
	    ZonedDateTime nextRun = now.with(targetTime);
	    
	    if (now.isAfter(nextRun)) {
	        nextRun = nextRun.plusDays(1);
	    }
	    
	    return Duration.between(now, nextRun).toMillis();
	}
}
