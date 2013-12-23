import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;


public class TestJson {
	
	public static void main(String[] args) throws Exception {
		
		InputStream downloadUrl = downloadUrl(getPath("天天向上"));
		System.out.println(parseXMLToString(downloadUrl,null));
	}
	
	private static String getPath(String searchKey) throws UnsupportedEncodingException{
		String keyword = URLEncoder.encode(searchKey, "UTF-8");
		return "http://58.17.218.61:9301/Mobile/moviesearch/httpcheck/tv/http/2/sort/1/PS/100/p/1/search/"+ keyword;
	}
	
	
    public static InputStream downloadUrl(String url) throws Exception {
		URL imageUrl = new URL(url);
		HttpURLConnection conn = (HttpURLConnection) imageUrl.openConnection();
		conn.setConnectTimeout(10000);
		conn.setReadTimeout(10000);
		conn.setInstanceFollowRedirects(true);
		InputStream is = conn.getInputStream();
		return is;
	}
    
	public static String parseXMLToString(InputStream inputStream,
			String encoding) {
		ByteArrayOutputStream swapStream = null;
		try {
			swapStream = new ByteArrayOutputStream();
			byte[] buff = new byte[1024];
			int rc = 0;
			while ((rc = inputStream.read(buff, 0, 512)) > 0) {
				swapStream.write(buff, 0, rc);
			}
			byte[] data = swapStream.toByteArray();
			if(encoding==null){
				return new String(data);
			}else{
				return new String(data, encoding);
			}	
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			try {
				if (swapStream != null)
					swapStream.close();
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		return null;
	}

}
