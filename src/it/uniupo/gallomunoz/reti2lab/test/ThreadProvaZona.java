package it.uniupo.gallomunoz.reti2lab.test;

import org.json.JSONObject;

import it.uniupo.gallomunoz.reti2lab.DynamicPage;
import it.uniupo.gallomunoz.reti2lab.Message;
import it.uniupo.gallomunoz.reti2lab.MessageQueue;
import it.uniupo.gallomunoz.reti2lab.SubjectTable;
import it.uniupo.gallomunoz.reti2lab.ZoneStatus;

public class ThreadProvaZona implements Runnable {
	
	ZoneStatus zs;
	DynamicPage dp;
	SubjectTable st;
	String uri;
	MessageQueue mq;
	Message message;
	JSONObject bodyJson;
	String state;
	
	public ThreadProvaZona(ZoneStatus zs, DynamicPage dp, SubjectTable st, String uri){
		this.zs = zs;
		this.dp = dp;
		this.st = st;
		this.uri = uri;
		mq = new MessageQueue(20);
	}
	
	@Override
	public void run() {
		//creiamo la entry nella dynamic page
		dp.createZone(zs);
		
		//creo la risorsa
		st.addSubject(uri);
		
		//e mi ci iscrivo
		try {
			st.subscribe(uri, mq);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		while(true){
			//rimane in attesa di un messaggio
			message = mq.receive();
			bodyJson = message.getBody();
			state = bodyJson.getString("stato");
			if(state.equals("off")){
				dp.publish(zs.zona, "false");
			}
			else if(state.equals("on")){
				dp.publish(zs.zona, "true");
			}
		}
	}

}
