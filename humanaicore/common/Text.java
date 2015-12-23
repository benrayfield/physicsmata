package humanaicore.common;
import java.io.UnsupportedEncodingException;

public class Text{
	private Text(){}
	
	public static byte[] stringToBytes(String s){
		try{
			return s.getBytes("UTF-8");
		}catch(UnsupportedEncodingException e){
			throw new RuntimeException(unicodeMessage);
		}
	}
	
	private static String unicodeMessage = "UTF-8 is standard for string encoding. Its simple definition is on Wikipedia and can be copied into this software if your version of Java doesn't support it. Each byte starts with 0, 10, 110, or 1110, used to find alignment at unknown position in data, and the rest are the data.";
	
	public static String bytesToString(byte b[]){
		try{
			return new String(b, "UTF-8");
		}catch(UnsupportedEncodingException e){
			throw new RuntimeException(unicodeMessage,e);
		}
	}

}
