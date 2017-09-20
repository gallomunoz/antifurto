package it.uniupo.gallomunoz.reti2lab;

import java.util.ArrayList;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * Questa classe implementa un demone che ascolta i messaggi diretti a tutte le zone
 * ed esegue le opportune azioni
 * 
 * @author 10031370
 *
 */


public class ZonesDaemon implements Runnable {
	
	private SubjectTable st;
	private MessageQueue mq;
	private Message m;
	private DynamicPage dp;
	
	ZonesDaemon(SubjectTable st, DynamicPage dp){
		this.st = st;
		this.dp = dp;
	}
	
	@Override
	public void run() {
		//Message Queue per la subject table
		mq = new MessageQueue(50);
		
		//crea la risorsa /zones nel caso non esista
		st.addSubject("/zones");
		
		//e ci si iscrive
		try {
			st.subscribe("/zones", mq);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		//ci mettiamo in ascolto
		while(true){
			m = mq.receive();
			
			switch(m.getMethod()){
			case "GET":
				doGet(m);
				break;
			case "PUT":
				doPut(m);
				break;
			default:
				break;
			}
			
			
		}

	}

	private void doGet(Message m) {
		try {
			st.MessageNotify(new Message("/zones", "", buildJSONFromDp()));
			System.out.println(buildJSONFromDp());
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}

	private void doPut(Message m) {
		if(m.getUri().equals("/zones")){		
			if(m.getBody().getString("allarmato").equals("off")){
				//messaggio di disallarmo globale di tutte le zone
				
				//TODO: Per ogni zona : manda messaggio di disallarmo
				for(ZoneStatus zs : dp.getDpHash().values()){
					
					m = new Message(zs.zona , "PUT", "{\"allarmato\":\"off\"}");
					try {
						st.MessageNotify(m);
					} catch (Exception e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}
				
			}else if(m.getBody().getString("allarmato").equals("on")){
				//messaggio di disallarmo globale di tutte le zone
				
				//TODO: Per ogni zona : manda messaggio di disallarmo
				for(ZoneStatus zs : dp.getDpHash().values()){
					System.out.println(zs.zona + " "+ "PUT" +" "+ "{\"allarmato\":\"on\"}");
					m = new Message(zs.zona , "PUT", "{\"allarmato\":\"on\"}");
					try {
						st.MessageNotify(m);
					} catch (Exception e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}
			}
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
