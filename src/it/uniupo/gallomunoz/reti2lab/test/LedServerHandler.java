package it.uniupo.gallomunoz.reti2lab.test;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;

import it.uniupo.gallomunoz.reti2lab.Message;

/**
 * 
 * @author 10031370
 * Questo handler ci permette di inviare messaggi con corpo JSON ad oggetti LedServer
 * Tramite chiamate REST
 */

public class LedServerHandler implements HttpHandler {
    //Questo oggetto identifica il nostro led
	private LedServer ledServer;
    
    public LedServerHandler(LedServer ledServer){
    	this.ledServer = ledServer;
    }
    
    
    /**
     * gestisce la richiesta http in base al metodo
     */
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
            		System.out.println("no GET no POST");
            		break;
            	}
    }
    
    private void doGet(HttpExchange he) throws IOException{
    	// otteniamo il JSON dal led, che corrispondera al body della risposta HTTP
    	String response = ledServer.getState().getBodyAsString();
    	//Stampa di servizio del server
    	System.out.println(he.getRequestMethod() + " eseguita da " +  he.getRemoteAddress().toString() + " sulla risorsa " + he.getRequestURI().toString());
		// impostiamo l'header della risposta HTTP
    	he.sendResponseHeaders(200, response.length());
    	//per scrivere il body della risposta ci serve il suo OutputStream
		OutputStream os = he.getResponseBody();
		//scriviamo il body della risposta HTTP
		os.write(response.getBytes());
		//risposta scritta, chiudiamo il suo OutputStream
		os.close();
    }
    
    private void doPost(HttpExchange he){
    	//Stampa di servizio del server
    	System.out.println(he.getRequestMethod() + " eseguita da " +  he.getRemoteAddress().toString() + " sulla risorsa " + he.getRequestURI().toString());
    }
    
    private void doPut(HttpExchange he) throws IOException{
    	//Stampa di servizio del server
    	System.out.println(he.getRequestMethod() + " eseguita da " +  he.getRemoteAddress().toString() + " sulla risorsa " + he.getRequestURI().toString());
		//ci serve un BufferedReader dalla richiesta HTTP
    	BufferedReader br = new BufferedReader(new InputStreamReader(he.getRequestBody()));
		try {
			//leggiamo il JSON dalla richiesta e lo impostiamo nel led tramite messaggio
			ledServer.setState(new Message(ledServer.getUri(), /*0,*/ br.readLine()));
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		// otteniamo il JSON dal led, che corrispondera al body della risposta HTTP
		String response = ledServer.getState().getBodyAsString();
		// impostiamo l'header della risposta HTTP
		he.sendResponseHeaders(200, response.length());
		//per scrivere il body della risposta ci serve il suo OutputStream
		OutputStream os = he.getResponseBody();
		//scriviamo il body della risposta HTTP
		os.write(response.getBytes());
		//risposta scritta, chiudiamo il suo OutputStream
		os.close();
    }
}