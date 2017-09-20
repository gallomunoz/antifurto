package it.uniupo.gallomunoz.reti2lab;

public class AttuatorDaemon implements Runnable {
	
	/**
	 * questo demone attiva un'attuatore tramite una specifica PUT
	 * come costruttore prende l'indirizzo della risorsa locale su cui ascoltare
	 * e l'URL della risorsa su cui fare la put
	 */
	
	private String attuatorURI;
	private SubjectTable st;
	private MessageQueue mq;
	private Message m;
	private String BBaddr;
	private String putStr;
	
	public AttuatorDaemon(String attuatorURI, SubjectTable st, String putStr){
		this.attuatorURI = attuatorURI;
		this.st = st;
		
		this.BBaddr = Tools.getBBaddr();
		this.putStr = putStr;
	}
	
	@Override
	public void run() {
		this.mq = new MessageQueue(20);
		//creiamo la risorsa  nella st
		st.addSubject(attuatorURI);
		
		//e ci iscriviamo
		try {
			st.subscribe(attuatorURI, mq);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		// ascoltiamo la risorsa
		while(true){
			m = mq.receive();
			
			//alcuni messaggi indirizzati all'attuatore non hanno il campo accensione
			if(m.getBody().has("accensione")){
				if(m.getBody().getBoolean("accensione")){
					//inviamo un messaggio http alla osso per innescare l'attuatore
					Tools.sendHttpMessage(BBaddr + putStr, "PUT", "");	
				}
			}
		}

	}

}
