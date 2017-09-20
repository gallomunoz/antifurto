package it.uniupo.gallomunoz.reti2lab.test;

import java.net.URI;

import it.uniupo.gallomunoz.reti2lab.Message;
import it.uniupo.gallomunoz.reti2lab.MessageQueue;
import it.uniupo.gallomunoz.reti2lab.SubjectTable;

public class TestConsumatoreSubscriber implements Runnable{
	
	@SuppressWarnings("unused")
	private Message m;
	private MessageQueue mq;
	URI uri;
	SubjectTable st;
	private int numMsgDaPrelevare = 10; //default 10
	private int deelay = 1000; // default 1 secondo
	
	public TestConsumatoreSubscriber(URI uri, SubjectTable st){
		this.uri = uri;
		this.st = st;
	}
	
	public TestConsumatoreSubscriber(int numMsgDaPrelevare, URI uri, SubjectTable st){
		this(uri, st);
		this.numMsgDaPrelevare = numMsgDaPrelevare;
	}
	
	public TestConsumatoreSubscriber(int numMsgDaPrelevare, int deelay, URI uri, SubjectTable st){
		this(numMsgDaPrelevare, uri, st);
		this.deelay = deelay;
	}

	@Override
	public void run() {
		//mi creo una messageQueue
		this.mq = new MessageQueue(10);
		
		//mi sottoscrivo nella subject table
		try {
			st.subscribe(uri.getPath(), mq);
		} catch (Exception e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		
		for(int cont = 0; cont < numMsgDaPrelevare; cont++){
			m = mq.receive();
			try{
				Thread.sleep(deelay);
			}catch(Exception e){
				e.printStackTrace();
			}
			
			//stampa messaggio
			System.out.println(m.getBodyAsString());
		}
	}

}
