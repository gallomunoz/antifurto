package it.uniupo.gallomunoz.reti2lab;


public class WebServerThread implements Runnable {
	
	public static int port = 12345;
	private DynamicPage dp;
	private SubjectTable st;
	
	public WebServerThread(DynamicPage dp, SubjectTable st){
		this.dp = dp;
		this.st = st;
	}
	
	@Override
	public void run() {
		SimpleHttpServer httpServer = new SimpleHttpServer();
		httpServer.Start(port, dp, st);
		
	}

}
