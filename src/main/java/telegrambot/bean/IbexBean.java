package telegrambot.bean;

public class IbexBean {
	String name;
	String date;
	float value;

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getDate() {
		return date;
	}

	public void setDate(String date) {
		this.date = date;
	}

	public float getValue() {
		return value;
	}

	public void setValue(float value) {
		this.value = value;
	}

	@Override
	public String toString() {
		return "IbexBean [name=" + name + ", date=" + date + ", value=" + value + "]";
	}
	
	public String output() {
		return name + ", " + date + ", " + value ;
	}

}
