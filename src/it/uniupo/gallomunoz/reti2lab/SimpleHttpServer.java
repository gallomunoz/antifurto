package it.uniupo.gallomunoz.reti2lab;

import java.io.IOException;
import java.net.InetSocketAddress;
import com.sun.net.httpserver.HttpServer;

public class SimpleHttpServer {
	private HttpServer server;

	public void Start(int port, DynamicPage dp, SubjectTable st) {
		try {
			server = HttpServer.create(new InetSocketAddress(port), 0);
			System.out.println("http server started at " + port);
			server.createContext("/",new Handlers.RootHandler(st));
			server.createContext("/zones", new Handlers.DynamicPageHandler(dp, st));
			
			server.setExecutor(null);
			server.start();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public void Stop() {
		server.stop(0);
		System.out.println("server stopped");
	}
}