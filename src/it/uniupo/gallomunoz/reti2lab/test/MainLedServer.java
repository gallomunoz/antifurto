package it.uniupo.gallomunoz.reti2lab.test;

public class MainLedServer {
	
	public static void main(String[] args) {
		int port; //porta su cui ascoltare http
		String uri = "/Devices/Leds/Led1"; //path del led
		LedServer led1 = new LedServer(uri, false); //nuovo led, spento per default
		LedHttpServer led1Http = new LedHttpServer(led1);
		port = Integer.parseInt(args[0]);
		led1Http.Start(port, uri);	
	}
}
