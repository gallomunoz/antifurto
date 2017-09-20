package it.uniupo.gallomunoz.reti2lab.test;

import it.uniupo.gallomunoz.reti2lab.MessageQueue;

public class MainTestMessageQueueSoloProduttore {
	


	public static void main(String[] args){
		Thread p;
		MessageQueue mq;
		
		mq = new MessageQueue(10);
		p = new Thread(new ProduttoreTest(100, mq));
		p.start();
		
	}
	
}
