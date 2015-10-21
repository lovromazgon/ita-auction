package feri.mazgon.auction.util;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.text.DateFormat;
import java.text.NumberFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;

import org.apache.commons.io.IOUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Component;

@Component
public class Util {
	private static ApplicationContext applicationContext;
	public static final String APP_ROOT = "/drazba";
	public static final DateFormat DATE_FORMAT = new SimpleDateFormat("dd.MM.yyyy HH:mm");
	
	@Autowired
	private void setServletContext(ApplicationContext servletContext) {
		Util.applicationContext = servletContext;
	}
	
	public static String formatMoney(long amount) {
		NumberFormat n = NumberFormat.getInstance();
		return n.format(amount / 100.0);
	}
	
	public static Long stringToLong(String amount) {
		NumberFormat n = NumberFormat.getInstance();
		Long result = null;
		try {
			result = (long)(n.parse(amount).doubleValue() * 100);
		} catch (ParseException e) {}
		return result;
	}
	
	public static String readFile(String path) {
		InputStream inputStream = null;
		String result = null;
		try {
			inputStream = applicationContext.getResource(path).getInputStream();
			BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream, "UTF-8"));
			
			StringBuilder sb = new StringBuilder();
			String line = "";
			
			while ((line = bufferedReader.readLine()) != null) {
				sb.append(line);
			}
			
			result = sb.toString();
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			if (inputStream != null) {
				try {
					inputStream.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
		return result;
	}
	
	public static byte[] readPicture(String path) {
		InputStream inputStream = null;
		byte[] result = null;
		try {
			inputStream = applicationContext.getResource(path).getInputStream();
			result = IOUtils.toByteArray(inputStream);
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			if (inputStream != null) {
				try {
					inputStream.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
		return result;
	}
	
	public static String formatDan(long days) {
		if (days == 1)
			return "dan";
		if (days == 2)
			return "dneva";
		
		return "dni";
	}
	
	public static String formatUra(long hours) {
		if (hours == 1)
			return "ura";
		if (hours == 2)
			return "uri";
		if (hours == 3 || hours == 4)
			return "ure";
		
		return "ur";
	}
	
	public static String formatMinuta(long minutes) {
		if (minutes == 1)
			return "minuta";
		if (minutes == 2)
			return "minuti";
		if (minutes == 3 || minutes == 4)
			return "minute";
		
		return "minut";
	}
}
