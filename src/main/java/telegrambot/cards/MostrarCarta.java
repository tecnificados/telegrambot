package telegrambot.cards;

import java.awt.BorderLayout;
import java.awt.GridLayout;
import java.awt.image.BufferedImage;
import java.util.List;
import javax.swing.ImageIcon;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;

public class MostrarCarta {
    public static void mostrar(BufferedImage carta) {
        JFrame frame = new JFrame("Vista previa de la carta");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(carta.getWidth() + 20, carta.getHeight() + 40);

        JLabel label = new JLabel(new ImageIcon(carta));
        frame.add(label, BorderLayout.CENTER);
        frame.setVisible(true);
    }
    
    public static void mostrarMazo(List<Carta> mazo) {
        JFrame frame = new JFrame("Mazo completo");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        // Panel con cuadrícula de 4 filas x 13 columnas
        JPanel panel = new JPanel(new GridLayout(4, 13, 2, 2));

        for (Carta carta : mazo) {
            JLabel label = new JLabel(new ImageIcon(carta.imagen));
            label.setToolTipText(carta.toString()); // Muestra "A de Corazones" al pasar el ratón
            panel.add(label);
        }

        frame.add(new JScrollPane(panel)); // por si la ventana es más pequeña que el mazo
        frame.pack();
        frame.setVisible(true);
    }
}

