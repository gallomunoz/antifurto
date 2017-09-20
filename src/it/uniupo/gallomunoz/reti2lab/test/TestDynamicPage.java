package it.uniupo.gallomunoz.reti2lab.test;

import java.util.ArrayList;

import it.uniupo.gallomunoz.reti2lab.DynamicPage;
import it.uniupo.gallomunoz.reti2lab.MyWebSocketClient;
import it.uniupo.gallomunoz.reti2lab.SubjectTable;
import it.uniupo.gallomunoz.reti2lab.WebServerThread;
import it.uniupo.gallomunoz.reti2lab.ZoneStatus;

public class TestDynamicPage {

	public static void main(String[] args) {
		DynamicPage dp = new DynamicPage();
		SubjectTable st = new SubjectTable();
		ArrayList<Thread> zoneThreads = new ArrayList<Thread>();
		
		MyWebSocketClient webSocketClient = new MyWebSocketClient(st);
		WebServerThread webServerThread = new WebServerThread(dp, st);
		ThreadProvaZona threadZona1 = new ThreadProvaZona(new ZoneStatus("1", "false"), dp, st, "/s1");
		ThreadProvaZona threadZona2 = new ThreadProvaZona(new ZoneStatus("2", "true"), dp, st, "/s2");
		Thread wst = new Thread(webServerThread, "webServer");
		Thread wsc = new Thread(webSocketClient, "webSocketClient");
		zoneThreads.add(new Thread(threadZona1, "z1"));
		zoneThreads.add(new Thread(threadZona2, "z2"));
		
		for(Thread t : zoneThreads){
			t.start();
		}
		wst.start();
		wsc.start();
		
		

	}

}
