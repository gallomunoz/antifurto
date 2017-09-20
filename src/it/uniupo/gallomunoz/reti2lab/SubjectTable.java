package it.uniupo.gallomunoz.reti2lab;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.Iterator;

/**
 * 
 * @author 10031370 10032452
 * 
 * questa classe rappresenta la subject table dell'agente
 *
 */
public class SubjectTable {
	private Subject subTabTree;
	
	/**
	 * il costruttore crea il nodo root di default, seza figli e senza mailbox
	 */
	public SubjectTable(){
			subTabTree = new Subject("");
	}
	
	/**
	 * @param r un albero subject su cui controllare
	 * @return true se e gia registrato
	 * 
	 * Controlla se un thread e gia registrato nell abero s
	 * in caso affermativo lo cancella
	 */
	
	private void fixSubscription(Subject r, MessageQueue mq){
		for(Subject son : r.sons ){
			//controllo che mq non sia nei subscribers
			Iterator<MessageQueue> i = son.subscribers.iterator();
			while(i.hasNext()){
				if(i.next().equals(mq)){ i.remove(); }
			}
			// se non ha figli caso base, stop.
			if(!son.sons.isEmpty()){
				//ricorsione fix su tutti i figli
				for(Subject grandSon : son.sons){
					fixSubscription(grandSon, mq);
				}
			}
			// se non c e sottoalbero e una foglia, quindi fine
		}
	}
	
	
	/**
	 * stampa tabella soggetti
	 */
	public void printSubjectTable(){
		//stampa sottoscrizioni figli
		System.out.println("");
		recPrintSubjectTable(subTabTree, 1);
		
	}
	
	/**
	 * @param s un soggetto radice dell albero
	 * @param l livello (serve per la stampa ordinata)
	 * 
	 * routine ricorsiva per stampa
	 */
	private void recPrintSubjectTable(Subject s,int l){
		printStringNtimes("> ", l);
		System.out.print(s.name + " : ");
		for(MessageQueue mq : s.subscribers){
			System.out.print("[ " + mq.getThreadId() + " ]");
		}
		System.out.println("");
		for(Subject son  : s.sons ){
			printStringNtimes("> " ,l+1);
			System.out.print(son.name + " : ");
			Iterator<MessageQueue> i = son.subscribers.iterator();
			while(i.hasNext()){
				System.out.print("[ " + i.next().getThreadId() + " ]");
			}
			System.out.println("");
			// se non ha figli caso base, stop.
			if(!son.sons.isEmpty()){
				//ricorsione fix su tutti i figli
				for(Subject grandSon : son.sons){
					recPrintSubjectTable(grandSon, l+2);
				}
			}
			// se non c e sottoalbero e una foglia, quindi fine
		}
	}
	
	/**
	 * routine per stampare i caratteri ripetuti
	 * @param s stringa da ripetere
	 * @param n numero di ripetizioni
	 */
	private void printStringNtimes(String s, int n){
		for(int i = 0; i < n; i++){
			System.out.print(s);
		}
	}
	
	/*
	 * subscribe
	 */
	
	/**
	 * 
	 * @param uri uri della sottoscrizione
	 * @param mq mq da fornire in fase di sottoscrizione
	 * @throws Exception
	 * 
	 * esegue una sottoscrizione verso un certa risorsa, se questa esiste
	 */
	public synchronized void subscribe(String uri, MessageQueue mq) throws Exception{
		//passo l uri alla procedura ricorsiva col solo path
		// es : URI = http://127.0.0.1:13000/Dev/L1?query=valore&...
		// path = /Dev/L1
		// in pratica tutto quello fra la porta e la query
		recSubscribe(URI.create(uri).getPath(), mq, subTabTree);
	}
	
	//routine ricorsiva subscribe
	private void recSubscribe(String uriPath, MessageQueue mq, Subject sub) throws Exception{
		boolean flag = false;
		String[] A = uriPath.split("/");
		if(A.length < 2){
			//sottoscrivo
			if(!sub.subscribers.contains(mq)){
				fixSubscription(sub, mq);
				sub.subscribers.add(mq);
			}
		}
		else{
			if(!sub.subscribers.contains(mq)){
				for(Subject son : sub.sons){
					if(son.name.equals(A[1])){
						flag = true;
						String relUri = URI.create(A[0] + "/").relativize(URI.create(uriPath)).getPath();
						System.out.println("Ho trovato " + A[1]);
						recSubscribe(relUri, mq, son);
					}
				}
				if(!flag){
						throw new Exception("Risorsa non trovata: " + A[1]);
				}
			}
			else{
				/**/
			}
		}
	}
	
	/**
	 * 
	 * @param uri uri della nuova risorsa
	 * 
	 * Aggiunge una nuova risorsa alla subject table
	 */
	public synchronized void addSubject(String uri){
		URI uriObject = URI.create(uri);
		try {
			recAddSubject(uriObject.getPath(), subTabTree);
		} catch (URISyntaxException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	private void recAddSubject(String uri, Subject s) throws URISyntaxException{
		URI uriObject = URI.create(uri);
		String A[] = uri.split("/");
		boolean flag = false;
		if(A.length > 1){
			// non siamo al nodo da creare
			for(Subject son : s.sons){
				if(son.name.equals(A[1])){
					//il nodo gia esiste
					flag = true;
					//System.out.println("chiamata ricorsiva" + A[0] + "/" + "su " + son.name);
					recAddSubject(new URI(A[0] + "/").relativize(uriObject).getPath(), son);
				}
			}
			//se il nodo non esiste ....
			if(flag == false){
				//creiamolo
				Subject newSub = new Subject(A[1]);
				System.out.println("creo il nuovo subject " + newSub.name);
				s.sons.add(newSub);
				recAddSubject(new URI(A[0] + "/").relativize(uriObject).getPath(), newSub);
				//System.out.println("chiamata ricorsiva " + A[0] + "/" + " su " + newSub.name);
			}
			//altrimenti nulla
			
		}
	}
	
	/**
	 * 
	 * @param m messaggio da notificare
	 * @throws Exception
	 * 
	 * esegue la notify di un certo messaggio
	 */
	public synchronized void MessageNotify(Message m) throws Exception{
		URI pathURI = URI.create(URI.create(m.getUri()).getPath());
		recNotify(m, subTabTree, pathURI);
	}
	
	
	private void recNotify(Message m, Subject sub, URI uri) throws Exception{
		String[] A = uri.getPath().split("/");
		boolean flag = false;
		for(MessageQueue mq : sub.subscribers){
			mq.send(m);
		}
		if(A.length > 1){	
			for(Subject son : sub.sons){
				if(son.name.equals(A[1])){
					flag = true;
					URI relUri = URI.create(A[0] + "/").relativize(uri);
					recNotify(m, son, relUri);
				}
			}
			if(!flag){
				/*errore, non esiste*/
				throw new Exception("La risorsa " + sub.name + " non esiste!");
			}
		}/*root o foglia*/
	}
	
	/**
	 * 
	 * @param threadId
	 * 
	 * Disiscrivi ThreadId da tutti i servizi a cui si e iscritto
	 */
	public synchronized void byebye(String threadId){
		System.out.println("[" + threadId + "] esegue Byebye");
		MessageQueue mq = new MessageQueue(10);
		fixSubscription(subTabTree, mq);
	}
}
