package it.uniupo.gallomunoz.reti2lab.test;

import static org.junit.Assert.*;

import java.sql.Timestamp;

import org.json.JSONException;
import org.json.JSONObject;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

import it.uniupo.gallomunoz.reti2lab.Message;

public class MessageTest {

	Message m;
	JSONObject o;
	Timestamp t;
	String uri = "/questo/e/un/URI";
	
	@Rule
	public final ExpectedException exception = ExpectedException.none();
	
	/*
	 * proviamo ad istanziare un messaggio con corpo vuoto
	 * e testiamo la funzione isBodyNull
	 */
	
	@Test
	public void testCorpoVuoto() {
		m = new Message(uri, "GET");
		assertTrue(m.isBodyNull());
	}
	
	/*
	 *  Proviamo ad instanziare un messaggio con corpo non vuoto
	 *  e relative prove dei metodi di stampa e isNotNull
	 */
	
	@Test
	public void testCorpoJsonCorretto(){
		m = new Message(uri, /*0,*/"GET",  "{\"stato\":\"on\"}");
		assertFalse(m.isBodyNull());
		System.out.println(m.getBodyAsString());
	}
	
	@Test
	public void testCorpoJsonMalFormato(){
		exception.expect(JSONException.class);
		m = new Message(uri, /*0,*/"GET", "{\"stato\"\"on\"}");
		assertFalse(m.isBodyNull());
		System.out.println(m.getBodyAsString());
	}
	
	@Test
	public void testTimestamp(){
		m = new Message(uri, /*0,*/"GET", "{\"stato\":\"on\"}");
		System.out.println(m.getTimeStamp().toString());
	}
	
	@Test
	public void testStampeVarie(){
		m = new Message(uri, /*0,*/"GET", "{\"stato\":\"on\"}");
		System.out.println("Test stampa URI da get: " + m.getUri());
	}
}
