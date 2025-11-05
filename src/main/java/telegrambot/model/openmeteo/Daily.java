package telegrambot.model.openmeteo;

import com.google.gson.annotations.SerializedName;

public class Daily {
	String[] time;

	@SerializedName("temperature_2m_max")
	double[] temperatureMax;

	@SerializedName("temperature_2m_min")
	double[] temperatureMin;

	@SerializedName("dew_point_2m_max")
	double[] dewPointMax;

	@SerializedName("dew_point_2m_min")
	double[] dewPointMin;

	public String[] getTime() {
		return time;
	}

	public void setTime(String[] time) {
		this.time = time;
	}

	public double[] getTemperatureMax() {
		return temperatureMax;
	}

	public void setTemperatureMax(double[] temperatureMax) {
		this.temperatureMax = temperatureMax;
	}

	public double[] getTemperatureMin() {
		return temperatureMin;
	}

	public void setTemperatureMin(double[] temperatureMin) {
		this.temperatureMin = temperatureMin;
	}

	public double[] getDewPointMax() {
		return dewPointMax;
	}

	public void setDewPointMax(double[] dewPointMax) {
		this.dewPointMax = dewPointMax;
	}

	public double[] getDewPointMin() {
		return dewPointMin;
	}

	public void setDewPointMin(double[] dewPointMin) {
		this.dewPointMin = dewPointMin;
	}
	
	
}
