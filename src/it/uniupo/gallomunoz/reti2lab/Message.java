package it.uniupo.gallomunoz.reti2lab;
import java.sql.Timestamp;

import org.json.*;

/**************************************************
 * 
 * @author 10031370 10032452
 * @version 1.0
 * Questa classe rappresenta un messaggio REST
 * con body JSON ( o nullo ) .
 * Se il body è vuoto, l'oggetto JSONBody è nullo
 *
 *************************************************/

public class Message {
	private String uri;
	private JSONObject body;
	private Timestamp timeStamp;
	//private int nSeq;
	private String method;
	
	/*****************************************************
	 * 
	 * @param uri    URI del mesaggio 
	 * @param nSeq   numero sequenza utile per i buffer
	 * 
	 ****************************************************/
	
	public Message(String uri/*, int nSeq*/, String method){
		this.uri = uri;
		body = null;
		timeStamp = new Timestamp(System.currentTimeMillis());
		this.method = method;
		//this.nSeq = nSeq;
	}
	
	/*****************************************************
	 * 
	 * @param uri    URI del mesaggio 
	 * @param nSeq   numero sequenza utile per i buffer
	 * @param body   Stringa JSON
	 * 
	 ****************************************************/
	
	public Message(String uri/*, int nSeq*/,String method, String body){
		this(uri/*, nSeq*/, method);
		this.body = new JSONObject(body);
	}
	
	/*****************************************************
	 * 
	 * @return l'URI del messaggio
	 * 
	 ****************************************************/
	
	public String getUri(){
		return uri;
	}
	
	/**
	 * 
	 * @return oggetto JSONObject
	 * 
	 */
	
	public JSONObject getBody(){
		return body;
	}
	
	/**
	 * 
	 * @return Stringa col body JSON
	 * 
	 */
	
	public String getBodyAsString(){
		return body.toString();
	}
	
	/**
	 * 
	 * @return oggetto timestamp
	 * 
	 */
	
	public Timestamp getTimeStamp(){
		return timeStamp;
	}
	
	
	/**
	 * 
	 * @return numero di sequenza
	 * 
	 */
	/*
	public int getNseq(){
		return nSeq;
	}*/
	
	public String getMethod(){
		return method;
	}
	
	/**
	 * 
	 * @return true se il corpo è nullo
	 */
	
	public boolean isBodyNull(){
		if(body == null) return true;
		else return false;
	}
}