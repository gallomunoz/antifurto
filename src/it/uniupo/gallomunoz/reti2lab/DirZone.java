package it.uniupo.gallomunoz.reti2lab;

/**
 * 
 * @author 10031370 10032452
 * 
 * Questa classe implementa una zona con "direzione"
 *
 */

public class DirZone implements Runnable {
	
	private String sensoreUri_1, sensoreUri_2, attuatoreUri, zoneUri;
	private SubjectTable st;
	private DynamicPage dp;
	private Message msg;
	private boolean allarmato;
	private ZoneStatus zs;
	private Message[] lastTwoMessages = new Message[2];
	
	/**
	 * 
	 * @param sensoreUri_1
	 * @param sensoreUri_2
	 * @param attuatoreUri
	 * @param st
	 * @param dp
	 * @param zoneUri : nome da assegnare a questa zona nella subject table
	 */
	
	public DirZone(String sensoreUri,
					  String sensoreUri2,
					  String attuatoreUri,
					  SubjectTable st,
					  DynamicPage dp,
					  String zoneUri,
					  ZoneStatus zs){
		this.sensoreUri_1 = sensoreUri;
		this.sensoreUri_2 = sensoreUri2;
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
		
		//ci iscriviamo come thread al sensore ed alle risorse rest
		//che identificano questa zona
		try {
			st.subscribe(zoneUri, mq);
			st.subscribe(sensoreUri_1, mq);
			st.subscribe(sensoreUri_2, mq);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		//ci mettiamo in ascolto
		while(true){
			msg = mq.receive();
			
			if(msg.getUri().equals(sensoreUri_1) || msg.getUri().equals(sensoreUri_2)){
				//messaggio da parte di uno dei 2 sensori
				if(msg.getBody().getString("status").equals("1") && allarmato){
					
					//l'ultimo messaggio diventa il penultimo
					lastTwoMessages[0] = lastTwoMessages[1];
					
					//questo è l'ultimo messaggio , lo mettiamo in lastTwoMessages[1]
					lastTwoMessages[1] = msg;
					
					//anzituttutto controlliamo che sian scattati almeno 2 attuatori
					if(lastTwoMessages[0] != null){
						//controlliamo l'ordine dei messaggi
						if(lastTwoMessages[0].getUri().equals(sensoreUri_1) &&
								lastTwoMessages[1].getUri().equals(sensoreUri_2)){
							/*
							 *  i sensori son scattati nel giusto ordine, attivo l'attuatore
							 */
							
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
