package it.uniupo.gallomunoz.reti2lab.test;

import static org.junit.Assert.*;

import java.net.URI;
import java.util.Arrays;

import org.json.JSONException;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

import it.uniupo.gallomunoz.reti2lab.MessageQueue;
import it.uniupo.gallomunoz.reti2lab.SubjectTable;

public class TestURI {
	
	URI uri;
	String[] path;
	URI uriPath;
	
	@Rule()
	public final ExpectedException exception = ExpectedException.none();
	
	@Test()
	public void test() throws Exception {
		/*
		uri = URI.create("http://127.0.0.1:12345/Dev/Lamps/Lamp1?campo_1=valore_1&campo_2=valore");
		System.out.println("URI: " + uri);
		System.out.println("PATH: " + uri.getPath());
		System.out.println("HOST: " + uri.getHost());
		System.out.println("PORT: " + uri.getPort());
		System.out.println("QUERY: " + uri.getQuery());
		System.out.println("RAW_QUERY: " + uri.getRawQuery());
		path = uri.getPath().split("/");
		System.out.println(Arrays.toString(path));
		System.out.println("**********stampa coi for**************");
		for(int i = 0; i < path.length; i++){
			System.out.println(path[i]);
		}
		
		System.out.println("**********test relativizazione**************");
		System.out.println(uriPath = URI.create(uri.getPath()) );
		path = uriPath.getPath().split("/");
		System.out.println("Lenght: " + path.length);
		System.out.println("prova-Jhonathan  " + URI.create("/").relativize(uriPath));
		System.out.println("" + URI.create("/Dev/").relativize(uriPath));
		System.out.println("" + URI.create("/Dev/Lamps").relativize(uriPath));
		System.out.println("" + URI.create("/Dev/Lamps/Lamp1").relativize(uriPath));
		
		
		System.out.println("**********test stampe uri particolari**************");
		System.out.println("uri vuoto: " + URI.create(""));
		System.out.println(URI.create("/").getPath().split("/").length);
		System.out.println(URI.create("L1"+"/").relativize(URI.create(" L1")));
		System.out.println("uri http://127.0.0.1:12345 relativizzato rispetto a / : " 
					+  URI.create("/").relativize(URI.create("http://127.0.0.1:12345/")).getPath());
		uri = URI.create("http://127.0.0.1:12345/");
		System.out.println("URI: " + uri);
		System.out.println("PATH: " + uri.getPath());
		uri = URI.create("http://127.0.0.1:12345");
		System.out.println("URI: " + uri);
		System.out.println("PATH: " + uri.getPath());
		
		System.out.println("**********test stampe uri particolari**************");
		
		*/
		//System.out.println("**********test subjectTable**************");
		//SubjectTable st = new SubjectTable();
		//st.printSubjectTable();
		//System.out.println("---------------------------------");
		//System.out.println("proviamo ad aggiungere un subject /Dev");
		//st.addSubject("http://127.0.0.1:13000/Dev");
		//st.printSubjectTable();
		//System.out.println("---------------------------------");
		//System.out.println("proviamo ad aggiungere un subject /Dev/Lamps");
		//st.addSubject("http://127.0.0.1:13000/Dev/Lamps");
		//st.printSubjectTable();
		//System.out.println("---------------------------------");
		//System.out.println("proviamo ad aggiungere un subject /Dev/Lamps/Lamp1");
		//st.addSubject("http://127.0.0.1:13000/Dev/Lamps/Lamp1");
		//st.printSubjectTable();
		//System.out.println("---------------------------------");
		//System.out.println("proviamo ad aggiungere un subject /Dev/Switches/Sw1");
		//st.addSubject("http://127.0.0.1:13000/Dev/Switches/Sw1");
		//st.printSubjectTable();
		//System.out.println("---------------------------------");
		//System.out.println("proviamo ad aggiungere un subject /Etc");
		//st.addSubject("http://127.0.0.1:13000/Etc");
		//st.printSubjectTable();
		//System.out.println("---------------------------------");
		//System.out.println("proviamo ad aggiungere un subject /Home");
		//st.addSubject("http://127.0.0.1:13000/Home");
		//st.printSubjectTable();
		//System.out.println("proviamo ad aggiungere un subject /Dev/Switches/Sw2");
		//st.addSubject("http://127.0.0.1:13000/Dev/Switches/Sw2");
		//st.printSubjectTable();
		/*
		MessageQueue mq = new MessageQueue(10);
		
		
		
		st.subscribe("http://127.0.0.1:13000/Dev/Lamps/Lamp1", mq);	
		st.subscribe("http://127.0.0.1:13000/Dev/Switches/Sw2", mq);
		st.subscribe("http://127.0.0.1:13000/Etc", mq);
		st.subscribe("http://127.0.0.1:13000/Home", mq);	
		st.printSubjectTable();
		st.byebye(Thread.currentThread().getName());		
		st.printSubjectTable();
		URI u = URI.create("Dev/Lamps/Lamp1");
		System.out.println(URI.create("Dev/").relativize(u));
		*/
		
		
		SubjectTable st = new SubjectTable();
		st.addSubject("http://127.0.0.1:13000/Dev/Switches/Sw1");
		st.printSubjectTable();
		
		
	}
	
	

}
