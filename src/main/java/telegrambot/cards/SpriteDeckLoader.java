package telegrambot.cards;

import javax.imageio.ImageIO;

import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class SpriteDeckLoader {

    private static final int COLS = 15; // cartas por fila
    private static final int ROWS = 5;  // filas (palos)

    private static final String[] PALOS = {"Corazones", "Picas", "Diamantes", "Tréboles"};
    private static final String[] VALORES = {
            "As", "2", "3", "4", "5", "6", "7", "8", "9", "10", "Jota", "Reina", "Rey"
    };

    public static List<Carta> loadDeck() throws IOException {
        // Carga desde resources
        BufferedImage spriteSheet = ImageIO.read(
                SpriteDeckLoader.class.getResourceAsStream("/cards3.png")
        );

        int totalAncho = spriteSheet.getWidth();
        int totalAlto = spriteSheet.getHeight();
        int cardWidth = totalAncho / COLS;
        int cardHeight = totalAlto / ROWS;

        //System.out.println("Tamaño detectado de cada carta: " + cardWidth + "x" + cardHeight);

        List<Carta> mazo = new ArrayList<Carta>();

        for (int fila = 0; fila < 4; fila++) {
            for (int col = 0; col < 13; col++) {
            	int x = col * cardWidth;
                int y = fila * cardHeight;

                BufferedImage cartaImg = spriteSheet.getSubimage(x, y, cardWidth, cardHeight);
                Carta carta = new Carta(VALORES[col], PALOS[fila], cartaImg);
                mazo.add(carta);
                
                //MostrarCarta.mostrar(carta.imagen);
            }
        }

        return mazo;
    }

    public static void main(String[] args) {
        try {
            List<Carta> mazo = loadDeck();
            System.out.println("Cartas cargadas: " + mazo.size());
            System.out.println("Ejemplo: " + mazo.get(0)); // As de Corazones
            
            //MostrarCarta.mostrar(mazo.get(0).imagen);
            MostrarCarta.mostrarMazo(mazo);
            
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}

