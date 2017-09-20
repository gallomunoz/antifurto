package it.uniupo.gallomunoz.reti2lab.test;

import it.uniupo.gallomunoz.reti2lab.Message;
import it.uniupo.gallomunoz.reti2lab.MessageQueue;

public class ConsumatoreTest implements Runnable{
	
	@SuppressWarnings("unused")
	private Message m;
	private MessageQueue mq;
	private int numMsgDaPrelevare = 10; //default 10
	private int deelay = 1000; // default 1 secondo
	
	public ConsumatoreTest(MessageQueue mq){
		this.mq = mq;
	}
	
	public ConsumatoreTest(MessageQueue mq, int numMsgDaPrelevare){
		this(mq);
		this.numMsgDaPrelevare = numMsgDaPrelevare;
	}
	
	public ConsumatoreTest(MessageQueue mq, int numMsgDaPrelevare, int deelay){
		this(mq, numMsgDaPrelevare);
		this.deelay = deelay;
	}

	@Override
	public void run() {
		for(int cont = 0; cont < numMsgDaPrelevare; cont++){
			m = mq.receive();
			try{
				Thread.sleep(deelay);
			}catch(Exception e){
				e.printStackTrace();
			}
		}
	}

}
