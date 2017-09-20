package it.uniupo.gallomunoz.reti2lab.test;

import java.net.URI;

import it.uniupo.gallomunoz.reti2lab.Message;
import it.uniupo.gallomunoz.reti2lab.MessageQueue;
import it.uniupo.gallomunoz.reti2lab.SubjectTable;

public class TestThreadSubscriber {
	public static void main(String[] args){
		SubjectTable st = new SubjectTable();
		st.addSubject("http://127.0.0.1:13000/Dev/Switches/Sw1");
		st.addSubject("http://127.0.0.1:13000/Dev/Lamps/Lamp1");
		TestConsumatoreSubscriber t1 = new TestConsumatoreSubscriber(
											URI.create("http://127.0.0.1:13000/Dev/Switches/Sw1"),
											st);
		TestConsumatoreSubscriber t2 = new TestConsumatoreSubscriber(
				URI.create("http://127.0.0.1:13000/Dev/Lamps/Lamp1"),
				st);
		TestConsumatoreSubscriber t3 = new TestConsumatoreSubscriber(
				URI.create("http://127.0.0.1:13000/Dev"),
				st);
		TestConsumatoreSubscriber t4 = new TestConsumatoreSubscriber(
				URI.create("http://127.0.0.1:13000/"),
				st);
		TestConsumatoreSubscriber t5 = new TestConsumatoreSubscriber(
				URI.create("http://127.0.0.1:13000/Dev/Switches/Sw1"),
				st);
		
		Thread thread1 = new Thread(t1, "thread1");
		Thread thread2 = new Thread(t2, "thread2");
		Thread thread3 = new Thread(t3, "thread3");
		Thread thread4 = new Thread(t4, "thread4");
		Thread thread5 = new Thread(t5, "thread5");
		
		
		thread1.start();
		thread2.start();
		thread3.start();
		thread4.start();
		thread5.start();
		
		try {
			st.MessageNotify(new Message("http://127.0.0.1:13000/Dev/Lamps/Lamp1", 
											"PUT",
											"{\"stato\":\"Lamp1 Accesa\"}"));
			
			st.MessageNotify(new Message("http://127.0.0.1:13000/Dev/Switches/Sw1", 
					"PUT",
					"{\"stato\":\"Sw1 Acceso\"}"));
			
			st.MessageNotify(new Message("http://127.0.0.1:13000/Home/User1", 
					"PUT",
					"{\"stato\":\"Messaggio che non verra recapitato\"}"));
			
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		try {
			Thread.sleep(10000);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		st.printSubjectTable();
	}
}
