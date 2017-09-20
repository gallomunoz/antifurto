package it.uniupo.gallomunoz.reti2lab;

/**
 * 
 * @author 10031370 10032452
 * 
 * Questa classe implementa una semplice zona per il nostro antifurto
 * Ascolta un singolo sensore, ed in risposta attiva un singolo attuatore.
 *
 *
 */

public class SimpleZone implements Runnable {
	
	private String sensoreUri, attuatoreUri, zoneUri;
	private SubjectTable st;
	private DynamicPage dp;
	private Message msg;
	private boolean allarmato;
	private ZoneStatus zs;
	
	/**
	 * 
	 * @param sensoreUri
	 * @param attuatoreUri
	 * @param st
	 * @param dp
	 * @param zoneUri : nome da assegnare a questa zona nella subject table
	 */
	
	public SimpleZone(String sensoreUri,
					  String attuatoreUri,
					  SubjectTable st,
					  DynamicPage dp,
					  String zoneUri,
					  ZoneStatus zs){
		this.sensoreUri = sensoreUri;
		this.attuatoreUri = attuatoreUri;
		this.dp = dp;
		this.st = st;
		this.zoneUri = zoneUri;
		this.allarmato = false; // disallarmata di default
		this.zs = zs;
		
	}
	
	@Override
	public void run() {
		
		MessageQueue mq = new MessageQueue(20);
		
		//creiamo la risorsa nella subject table
		st.addSubject(zoneUri);
		
		//creiamo un record nella dynamic page
		dp.createZone(zs);
		
		//ci iscriviamo come thread al sensore ed alla risorsa rest
		//che identifica questa zona
		try {
			st.subscribe(zoneUri, mq);
			st.subscribe(sensoreUri, mq);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		//ci mettiamo in ascolto
		while(true){
			msg = mq.receive();
			
			if(msg.getUri().equals(sensoreUri)){
				//messaggio da parte del sensore
				if(msg.getBody().getString("status").equals("1") && allarmato){
					
					//assembliamo un messaggio
					msg = new Message(
							attuatoreUri,
							"PUT",
							"{\"accensione\":\"true\"}"
							);
					
					//facciamo la notify verso l'attuatore
					try {
						st.MessageNotify(msg);
					} catch (Exception e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
					
				}
			}
			if(msg.getUri().equals(zoneUri)){
				//messaggio da parte della zona
				switch(msg.getBody().getString("allarmato")){
					case "on":
						allarmato = true;
						dp.publish(zoneUri, "{\"allarmato\":\"on\"}");
					try {
						st.MessageNotify(new Message(/*"/updates"*/"/", "", "{\"allarmato\":\"on\",\"risorsa\":\"" + zoneUri + "\"}"));
					} catch (Exception e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
						break;
					case "off":
						allarmato = false;
						dp.publish(zoneUri, "{\"allarmato\":\"off\"}");
					try {
						st.MessageNotify(new Message(/*"/updates"*/"/", "", "{\"allarmato\":\"off\",\"risorsa\":\"" + zoneUri + "\"}"));
					} catch (Exception e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
						break;
					default:
						break;
					
				}
			}
			
			
		}
		
		
		
	}

}
