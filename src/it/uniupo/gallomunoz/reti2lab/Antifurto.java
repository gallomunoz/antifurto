package it.uniupo.gallomunoz.reti2lab;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Calendar;
import java.util.Timer;


public class Antifurto {
	private static boolean timerIsOn;
	private static int oraAllarmo;
	private static int oraDisallarmo;
	private static int minAllarmo;
	private static int minDisallarmo;
	private static int secAllarmo;
	private static int secDisallarmo;

	public static void main(String[] args) {
		DynamicPage dp = new DynamicPage();
		SubjectTable st = new SubjectTable();
		Timer timerAllarmato, timerDisallarmato;
		Calendar calendar = Calendar.getInstance();
		
		
		/*
		 * definisco un subject per notificare i cambi di stato
		 * utile per chi deve ascoltarle, come i client esterni
		 */
		
		//st.addSubject("/updates");
		

		/*
		 * definisco i sensori nella subject table
		 */
		
		st.addSubject("/sensors/s1");
		st.addSubject("/sensors/s2");
		st.addSubject("/sensors/s3");
		
		/*
		 * definisco e lancio i threads per :
		 *  i) web socket client
		 *  ii) web server
		 *  iii)demoni per gli attuatori
		 *  iv) demone per gestire la comunicazione col brocker mqtt
		 */
		
		MyWebSocketClient webSocketClient = new MyWebSocketClient(st);
		WebServerThread webServerThread = new WebServerThread(dp, st);
		AttuatorDaemon sirenaDaemon = new AttuatorDaemon("/attuators/sirena",
														 st,
														 "/horus-ws/Devices/SO_Sirena/ON.json");
		AttuatorDaemon lampeggiatoreDaemon = new AttuatorDaemon("/attuators/lampeggiatore",
																st,
																"/horus-ws/Devices/SO_Lampeggiatore/ON.json");
		MyMqttClientThread myClientMqttThread = new MyMqttClientThread(st);
		Thread wst = new Thread(webServerThread, "webServer");
		Thread wsc = new Thread(webSocketClient, "webSocketClient");
		Thread sdt = new Thread(sirenaDaemon, "sirenaDaemon");
		Thread ldt = new Thread(lampeggiatoreDaemon, "lampeggiatoreDaemon");
		Thread mct = new Thread(myClientMqttThread, "MqttDaemon");
		
		wst.setDaemon(true);
		wsc.setDaemon(true);
		sdt.setDaemon(true);
		ldt.setDaemon(true);
		mct.setDaemon(true);
		
		wst.start();
		wsc.start();
		sdt.start();
		ldt.start();
		mct.start();
		
		/*
		 * definisco e lancio i thread per le zone
		 */
		
		SimpleZone zone1 = new SimpleZone("/sensors/s1",
										  "/attuators/sirena",
										  st,
										  dp,
										  "/zones/z1",
										  new ZoneStatus("/zones/z1", "{\"allarmato\":\"off\"}"));
		
		SimpleZone zone2 = new SimpleZone("/sensors/s2",
				  "/attuators/lampeggiatore",
				  st,
				  dp,
				  "/zones/z2",
				  new ZoneStatus("/zones/z2", "{\"allarmato\":\"off\"}"));
		
		SimpleZone zone3 = new SimpleZone("/sensors/s3",
				  "/attuators/lampeggiatore",
				  st,
				  dp,
				  "/zones/z3",
				  new ZoneStatus("/zones/z3", "{\"allarmato\":\"off\"}"));
		
		DirZone zone4 = new DirZone("/sensors/s1",
									"/sensors/s2",
									"/attuators/sirena",
									st,
									dp,
									"/zones/z4",
									new ZoneStatus("/zones/z4", "{\"allarmato\":\"off\"}"));
		
		//demone che rappresenta /zones
		ZonesDaemon zd = new ZonesDaemon(st, dp);
		
		Thread ThreadZone1 = new Thread(zone1, "zone1");
		Thread ThreadZone2 = new Thread(zone2, "zone2");
		Thread ThreadZone3 = new Thread(zone3, "zone3");
		Thread ThreadZone4 = new Thread(zone4, "zone4");
		Thread tzd = new Thread(zd, "zones");
		
		ThreadZone1.start();
		ThreadZone2.start();
		ThreadZone3.start();
		ThreadZone4.start();
		tzd.setDaemon(true);
		tzd.start();
		
		
		//sleep prima di settare timer e stampa subject table
		//per non fare pasticci con le println
		
				try {
					Thread.sleep(5000);
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
		
		
		/*
		 * timer
		 */
		
		getTimerCfg();
		if(timerIsOn){
			/*
			 * definiamo 2 timer, per allarmare e disallarmare a certi
			 * orari
			 */
			
			// timer per l'allarmo
			calendar.set(Calendar.HOUR_OF_DAY, oraAllarmo);
			calendar.set(Calendar.MINUTE, minAllarmo);
			calendar.set(Calendar.SECOND, secAllarmo);
			
			timerAllarmato = new Timer();
			timerAllarmato.schedule(new MessageTimeTask(st, new Message("/zones", "PUT", "{\"allarmato\":\"on\"}")),
														calendar.getTime());
			
			//timer per disallarmo
			calendar = Calendar.getInstance();
			calendar.set(Calendar.HOUR_OF_DAY, oraDisallarmo);
			calendar.set(Calendar.MINUTE, minDisallarmo);
			calendar.set(Calendar.SECOND, secDisallarmo);
			
			timerDisallarmato = new Timer();
			timerDisallarmato.schedule(new MessageTimeTask(st, new Message("/zones", "PUT", "{\"allarmato\":\"off\"}")),
														calendar.getTime());
			
			System.out.println("Timer Attivato : Allarmo ore " + oraAllarmo +
								" - " + minAllarmo + " - " + secAllarmo +
								", Disallarmo ore " + oraDisallarmo + " - "
								+ minDisallarmo + " - " + secDisallarmo);		
			}
		
		
		st.printSubjectTable();

	}
	
	/**
	 * legge i dati di configurazione dal file mqttBroker.cfg utili per collegarsi al brocker
	 */
	
	public static void getTimerCfg(){
		File file = new File("timer.cfg");
		if (file.exists() && !file.isDirectory()) {
			BufferedReader reader = null;
			try {
				reader = new BufferedReader(new InputStreamReader(new FileInputStream(file)));
			} catch (FileNotFoundException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			for(int i = 0; i<14; i++){
				switch(i){
					case 1:
					try {
						switch(reader.readLine()){
							case "true":
								timerIsOn = true;
								break;
							case "false":
								timerIsOn = false;
								break;
						}
					} catch (IOException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
						break;
					case 3:
						try {
							oraAllarmo = Integer.parseInt(reader.readLine());
						} catch (IOException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
							break;
					case 5:
						try {
							minAllarmo = Integer.parseInt(reader.readLine());
						} catch (IOException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
						break;
					case 7:
						try {
							secAllarmo = Integer.parseInt(reader.readLine());
						} catch (IOException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
						break;
					case 9:
						try {
							oraDisallarmo = Integer.parseInt(reader.readLine());
						} catch (IOException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
						break;
					case 11:
						try {
							minDisallarmo = Integer.parseInt(reader.readLine());
						} catch (IOException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
						break;
					case 13:
						try {
							secDisallarmo = Integer.parseInt(reader.readLine());
						} catch (IOException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
						break;
					default:
					try {
						reader.readLine();
					} catch (IOException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
						break;
							
				}
			}
				try {
					reader.close();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
		}
	}

}
