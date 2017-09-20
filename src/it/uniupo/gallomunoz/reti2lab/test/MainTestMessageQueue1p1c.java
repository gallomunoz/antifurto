package it.uniupo.gallomunoz.reti2lab.test;

import it.uniupo.gallomunoz.reti2lab.MessageQueue;

public class MainTestMessageQueue1p1c {
	public static void main(String[] args){
		final int deelayProduttore = 10;
		final int deelayConsumatore = 50;
		final int qntDaProdurre = 100;
		final int qntDaConsumare = 30;
		Thread p, c;
		MessageQueue mq;
		
		mq = new MessageQueue(10);
		p = new Thread(new ProduttoreTest(qntDaProdurre, mq, deelayProduttore));
		c = new Thread(new ConsumatoreTest(mq, qntDaConsumare, deelayConsumatore));
		
		p.start();
		c.start();
	}
}
