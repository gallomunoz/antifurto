package it.uniupo.gallomunoz.reti2lab;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Paths;

public class Tools {
	// convert InputStream to String
		public static String getStringFromInputStream(InputStream is) {

			BufferedReader br = null;
			StringBuilder sb = new StringBuilder();

			String line;
			try {

				br = new BufferedReader(new InputStreamReader(is));
				while ((line = br.readLine()) != null) {
					sb.append(line);
				}

			} catch (IOException e) {
				e.printStackTrace();
			} finally {
				if (br != null) {
					try {
						br.close();
					} catch (IOException e) {
						e.printStackTrace();
					}
				}
			}

			return sb.toString();

		}
		
		public static void sendHttpMessage(String uri, String method, String body){
			URL url;
			try {
				url = new URL(uri);
				HttpURLConnection connection = (HttpURLConnection) url.openConnection();
				String data = body;//corpo della richiesta
		        connection.setDoOutput(true);//abilita la scrittura
		        connection.setRequestMethod(method);//settaggio del metodo
		       
		        OutputStreamWriter wr = new OutputStreamWriter(connection.getOutputStream());
		        wr.write(data);//scrittura del content
		        wr.flush();
		        wr.close();
		        
		        connection.getResponseCode();
				
			} catch (MalformedURLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}		
		}
		
		public static String getBBaddr(){
			String ret = "";
			File file = new File("./bbUrl.cfg");
			if (file.exists() && !file.isDirectory()) {
				BufferedReader reader;
				try {
					reader = new BufferedReader(new InputStreamReader(new FileInputStream(file)));
					String line = new String();
					while ((line = reader.readLine()) != null) {
						if (!line.startsWith("#") && !line.isEmpty()) {
								ret = line;
						}
					}
					reader.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
			return ret;
		}
		
		static String readFile(String path, Charset encoding) 
				  throws IOException 
				{
				  byte[] encoded = Files.readAllBytes(Paths.get(path));
				  return new String(encoded, encoding);
				}

}
