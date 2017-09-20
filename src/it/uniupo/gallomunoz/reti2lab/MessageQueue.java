package it.uniupo.gallomunoz.reti2lab;

/**
 * 
 * @author 10031370 10032452
 * @version 1.0
 * 
 * Questa classe implementa un buffer circolare
 * per la gestione della coda di messaggi
 *
 */

public class MessageQueue {
	private Message[] queue;
	
	//puntatore scrittura
	private int wp;
	
	//puntatore lettura
	private int rp;
	
	private String threadID;
	
	/**
	 * 
	 * @param dimBuffer dimensione del buffer
	 * 
	 * puntatori inizializzati a zero
	 * 
	 */
	
	public MessageQueue(int dimBuffer){
		queue = new Message[dimBuffer];
		wp = 0;
		rp = 0;
		threadID = Thread.currentThread().getName();
	}
	
	/**
	 * 
	 * @param m un messaggio
	 * 
	 * invia il messaggio m alla coda circolare
	 * 
	 */
	public synchronized void send(Message m){
		queue[wp] = m;
		notifyAll(); //annuncia di aver prodotto qualcosa
		System.out.println("*Send* URI: " + m.getUri() + " Body: " + m.getBodyAsString() + " posizione: " + wp);
		wp += 1;
		wp = wp % queue.length; //coda circolare
	}
	
	/**
	 * 
	 * @return un messaggio
	 * 
	 * ricevi un messaggio dalla coda circolare
	 */
	public synchronized Message receive(){
		while(queue[rp] == null){
			
			//non c'è contenuto, lo attendiamo
			try{
				System.out.println("[" + Thread.currentThread().getName() + "]" + "*Receive* in attesa su " + rp);
				wait();
			}catch(Exception e){
				e.printStackTrace();
			}
		}
		
		//c'è del contenuto, lo consumiamo
		Message m = queue[rp];
		queue[rp] = null;
		System.out.println("[" + Thread.currentThread().getName() + "]" + "*Receive* URI: " + m.getUri() + " Body: " +  m.getBodyAsString() + " posizione: " + rp);
		rp +=1;
		rp = rp % queue.length; //coda circolare
		return m;
	}
	
	
	public String getThreadId(){
		return threadID;
	}
	
	/**
	 * per noi 2 MessageQueue sono uguali quando appartengono allo stesso thread
	 */
	public boolean equals(Object o){
		if(((MessageQueue)o).getThreadId().equals(threadID)){ return true; }
		else { return false; }
	}

}
