package it.uniupo.gallomunoz.reti2lab;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.sun.net.httpserver.Headers;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;

public class Handlers {
	public static class RootHandler implements HttpHandler {
		
		SubjectTable st;
		
		public RootHandler(SubjectTable st){
			this.st = st;
		}

		@Override
		public void handle(HttpExchange he) throws IOException {
			String method = he.getRequestMethod();
            switch(method){
            	case "GET": doGet(he);
            		break;
            	case "POST": doPost(he);
            		break;
            	case "PUT": doPut(he);
            		break;
            	default:
            		System.out.println(he.getRequestMethod());
            		break;
            	}
		}

		private void doPut(HttpExchange he) {
//			System.out.println("PUT su : " + he.getRequestURI().toString());
//			//Eseguo la notify che riguarda il path della richiesta
//			InputStream io = he.getRequestBody();
//			String body = Tools.getStringFromInputStream(io);
//			try {
//				st.MessageNotify(new Message(he.getRequestURI().toString(), "PUT", body));
//			} catch (Exception e) {
//				// TODO Auto-generated catch block
//				e.printStackTrace();
//			}
//			
//			JSONObject jsonBodyRisposta = new JSONObject(body);
//			jsonBodyRisposta.accumulate("risorsa", he.getRequestURI().getPath());
//			
//			String response = jsonBodyRisposta.toString();
//			
//			try {
//				he.sendResponseHeaders(200, response.length());
//				OutputStream os = he.getResponseBody();
//				os.write(response.toString().getBytes());
//				os.close();
//			} catch (IOException e) {
//				// TODO Auto-generated catch block
//				e.printStackTrace();
//			}
//			System.out.println(body);
		}

		private void doPost(HttpExchange he) {
			// TODO Auto-generated method stub
			
		}

		private void doGet(HttpExchange he) {
			
			String response = "";
			try {
				try {
					response = Tools.readFile( new URI("/").relativize(he.getRequestURI()).toString() ,
												StandardCharsets.UTF_8);
				} catch (URISyntaxException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			} catch (IOException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
			// impostiamo l'header della risposta HTTP
	    	try {
				he.sendResponseHeaders(200, response.length());
				//per scrivere il body della risposta ci serve il suo OutputStream
				OutputStream os = he.getResponseBody();
				//scriviamo il body della risposta HTTP
				os.write(response.getBytes());
				//risposta scritta, chiudiamo il suo OutputStream
				os.close();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
		}
	}

	public static class EchoHeaderHandler implements HttpHandler {

		@Override
		public void handle(HttpExchange he) throws IOException {
			Headers headers = he.getRequestHeaders();
			Set<Map.Entry<String, List<String>>> entries = headers.entrySet();
			String response = "";
			for (Map.Entry<String, List<String>> entry : entries)
				response += entry.toString() + "\n";
			he.sendResponseHeaders(200, response.length());
			OutputStream os = he.getResponseBody();
			os.write(response.toString().getBytes());
			os.close();
		}
	}

	public static class EchoGetHandler implements HttpHandler {

		@Override
		public void handle(HttpExchange he) throws IOException {
			// parse request
			Map<String, Object> parameters = new HashMap<String, Object>();
			URI requestedUri = he.getRequestURI();
			String query = requestedUri.getRawQuery();
			parseQuery(query, parameters);
			// send response
			String response = "";
			for (String key : parameters.keySet())
				response += key + " = " + parameters.get(key) + "\n";
			he.sendResponseHeaders(200, response.length());
			OutputStream os = he.getResponseBody();
			os.write(response.toString().getBytes());
			os.close();
		}

	}

	public static class EchoPostHandler implements HttpHandler {

		@Override
		public void handle(HttpExchange he) throws IOException {
			System.out.println("Served by /echoPost handler...");
			// parse request
			Map<String, Object> parameters = new HashMap<String, Object>();
			InputStreamReader isr = new InputStreamReader(he.getRequestBody(), "utf-8");
			BufferedReader br = new BufferedReader(isr);
			String query = br.readLine();
			parseQuery(query, parameters);
			// send response
			String response = "";
			for (String key : parameters.keySet())
				response += key + " = " + parameters.get(key) + "\n";
			he.sendResponseHeaders(200, response.length());
			OutputStream os = he.getResponseBody();
			os.write(response.toString().getBytes());
			os.close();

		}
	}
	
	public static class DynamicPageHandler implements HttpHandler{
		DynamicPage dp;
		SubjectTable st;
		
		public DynamicPageHandler(DynamicPage dp, SubjectTable st){
			this.dp = dp;
			this.st = st;
		}
		
		@Override
		public void handle(HttpExchange he) throws IOException {
			String response, method;
			OutputStream os;
			
			method = he.getRequestMethod(); 
			
			switch(method){
				case "GET":
					System.out.println("GET su zones");
					response = buildJSONFromDp();
					// impostiamo l'header della risposta HTTP
			    	he.sendResponseHeaders(200, response.length());
			    	//per scrivere il body della risposta ci serve il suo OutputStream
					os = he.getResponseBody();
					//scriviamo il body della risposta HTTP
					os.write(response.getBytes());
					//risposta scritta, chiudiamo il suo OutputStream
					os.close();
					break;
				
				case "PUT":
					System.out.println("PUT su : " + he.getRequestURI().toString());
					//Eseguo la notify che riguarda il path della richiesta
					InputStream io = he.getRequestBody();
					String body = Tools.getStringFromInputStream(io);
					try {
						st.MessageNotify(new Message(he.getRequestURI().toString(), "PUT", body));
					} catch (Exception e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
					
					JSONObject jsonBodyRisposta = new JSONObject(body);
					jsonBodyRisposta.accumulate("risorsa", he.getRequestURI().getPath());
					
					response = jsonBodyRisposta.toString();
					
					try {
						he.sendResponseHeaders(200, response.length());
						os = he.getResponseBody();
						os.write(response.toString().getBytes());
						os.close();
					} catch (IOException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
					System.out.println(body);
					break;
				
				default:
					break;
			}
			
		}

		private String buildJSONFromDp() {
			//String str = "";
			ArrayList<JSONObject> jsonArray = new ArrayList<JSONObject>();
			JSONObject jsonObj = new JSONObject();
			for(ZoneStatus zs : dp.getDpHash().values()){
				//str = str + "zona: " + zs.zona + ", valore: " + zs.status + "</br>";
				try{
					jsonArray.add(new JSONObject("{\"" + zs.zona + "\":\"" + new JSONObject(zs.status).getString("allarmato") + "\"}"));
				}catch(JSONException JSONex){
					JSONex.printStackTrace();
				}
			}
			jsonObj.put("zones", new JSONArray(jsonArray));
			
			return jsonObj.toString();
		}
		
	}

	@SuppressWarnings("unchecked")
	public static void parseQuery(String query, Map<String, Object> parameters) throws UnsupportedEncodingException {

		if (query != null) {
			String pairs[] = query.split("[&]");

			for (String pair : pairs) {
				String param[] = pair.split("[=]");

				String key = null;
				String value = null;
				if (param.length > 0) {
					key = URLDecoder.decode(param[0], System.getProperty("file.encoding"));
				}

				if (param.length > 1) {
					value = URLDecoder.decode(param[1], System.getProperty("file.encoding"));
				}

				if (parameters.containsKey(key)) {
					Object obj = parameters.get(key);
					if (obj instanceof List<?>) {
						List<String> values = (List<String>) obj;
						values.add(value);
					} else if (obj instanceof String) {
						List<String> values = new ArrayList<String>();
						values.add((String) obj);
						values.add(value);
						parameters.put(key, values);
					}
				} else {
					parameters.put(key, value);
				}
			}
		}
	}
}
