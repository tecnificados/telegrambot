package telegrambot.cards;

import java.awt.image.BufferedImage;

public class Carta {
	String valor;
	String palo;
	BufferedImage imagen;

	public Carta(String valor, String palo, BufferedImage imagen) {
		this.valor = valor;
		this.palo = palo;
		this.imagen = imagen;
	}

	@Override
	public String toString() {
		return valor + " de " + palo;
	}

	public String getValor() {
		return valor;
	}

	public void setValor(String valor) {
		this.valor = valor;
	}

	public String getPalo() {
		return palo;
	}

	public void setPalo(String palo) {
		this.palo = palo;
	}

	public BufferedImage getImagen() {
		return imagen;
	}

	public void setImagen(BufferedImage imagen) {
		this.imagen = imagen;
	}

}
