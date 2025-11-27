package telegrambot.bean;

import java.text.SimpleDateFormat;
import java.util.Date;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class IbexBean {
	
	private static final Logger logger = LoggerFactory.getLogger(IbexBean.class);
	
	static SimpleDateFormat formatterOuput = new SimpleDateFormat("dd/MM/yyyy HH:mm");
	static SimpleDateFormat formatterInput=new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss");
	
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
		String coolDate="";
		try {
			Date date2 = formatterInput.parse(date);
			coolDate=formatterOuput.format(date2);
		}
		catch (Exception e) {
			logger.error("Error parsing date", e);
		}
		
		//return name + ", " + date + ", " + value ;
		 return "<b>" + name + "</b>\n" +
         "📅 " + coolDate + " " +
         "💶 <b>" + String.format("%.2f", value) + "</b>\n";
	}

}
