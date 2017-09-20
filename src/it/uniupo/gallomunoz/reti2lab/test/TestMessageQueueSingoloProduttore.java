package it.uniupo.gallomunoz.reti2lab.test;

import java.util.ArrayList;

import org.junit.Test;

import it.uniupo.gallomunoz.reti2lab.MessageQueue;

public class TestMessageQueueSingoloProduttore {

	Thread p;
	ArrayList<Thread> Consumatori;
	MessageQueue mq;
	
	@Test
	public void testSoloProduttore() {
		mq = new MessageQueue(10);
		p = new Thread(new ProduttoreTest(100, mq));
		//p.setDaemon(true);
		System.out.println("prova");
		p.start();
		System.out.println("prova2");
	}

}
