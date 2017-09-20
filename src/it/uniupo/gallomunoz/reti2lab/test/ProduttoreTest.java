package it.uniupo.gallomunoz.reti2lab.test;

import it.uniupo.gallomunoz.reti2lab.Message;
import it.uniupo.gallomunoz.reti2lab.MessageQueue;

/**
	 * @author 10031370 10032452
	 *
	 * Questo produttore crea un messaggio del tipo
	 * 
	 * {"numero":"x"} con x intero da zero in su
	 * 
	 * nota : x == nSeq;
	 */
	
public class ProduttoreTest implements Runnable {
	
	int qntDaprodure;
	Message m;
	MessageQueue mq;
	int deelay = 1000; // default 1 secondo
	
	/**
	 * 
	 * @param qnt quantità di messaggi da produrre
	 * @param mq  una MessageQueue su cui produrre
	 * 
	 */
	
	public ProduttoreTest(int qnt, MessageQueue mq){
		qntDaprodure = qnt;
		this.mq = mq;
	}
	
	/**
	 * 
	 * @param qnt quantità di messaggi da produrre
	 * @param mq  una MessageQueue su cui produrre
	 * @param deelay ritardo indotto tra una produzione e l'altra
	 */
	
	public ProduttoreTest(int qnt, MessageQueue mq,int deelay){
		this(qnt, mq);
		this.deelay = deelay;
	}
	
	
	
	@Override
	public void run() {
		//System.out.println("prova");
		for(int cont = 0; cont < qntDaprodure; cont++){
			try {
				Thread.sleep(deelay);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			//System.out.println("prova");
			System.out.print("[" + Thread.currentThread().getName() + "]");
			m = new Message("/sono/il/produttore",
					/*cont,*/"GET", "{\"numero\":\"" + cont + "\"}");
			mq.send(m);
		}
		System.out.println("Lavoro del produttore finito !");
	}
	
}
