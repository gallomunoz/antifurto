package it.uniupo.gallomunoz.reti2lab.test;

import it.uniupo.gallomunoz.reti2lab.Message;

public class LedServer {
	private boolean led;
	private String uri;
	private int nSeq;
	
	public LedServer(String uri, boolean stato){
		led = stato;
		this.uri = uri;
		nSeq = 0;
	}
	
	public synchronized Message getState(){
		Message msg;
		String body = "{\"stato\":\"";
		if(led){
			body = body + "on\"}";
		}else body = body + "off\"}";
		msg = new Message(uri, /*nSeq,*/ body);
		nSeq += 1;
		return msg;
	}
	
	public synchronized void setState(Message msg) throws Exception{
		String stato = msg.getBody().getString("stato");
		switch(stato){
			case "on":
				led = true;
				break;
			case "off":
				led = false;
				break;
			default:
				throw new Exception("stato led non valido");
		}
	}
	
	public String getUri(){
		return uri;
	}

}
