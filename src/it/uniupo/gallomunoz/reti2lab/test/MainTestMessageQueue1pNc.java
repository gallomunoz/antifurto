package it.uniupo.gallomunoz.reti2lab.test;

import java.util.ArrayList;

import it.uniupo.gallomunoz.reti2lab.MessageQueue;

public class MainTestMessageQueue1pNc {
	public static void main(String[] args){
		final int numConsumatori = 100;
		final int deelayProduttore = 20;
		final int deelayConsumatore = 80;
		Thread p;
		MessageQueue mq;
		ArrayList<Thread> c = new ArrayList<Thread>();
		
		mq = new MessageQueue(10);
		p = new Thread(new ProduttoreTest(155, mq, deelayProduttore));
		for(int cont = 0; cont < numConsumatori; cont++){
			c.add(new Thread(new ConsumatoreTest(mq, 30, deelayConsumatore)));
		}
		
		for(Thread t : c){
			t.start();
		}
		p.start();
	}
}
