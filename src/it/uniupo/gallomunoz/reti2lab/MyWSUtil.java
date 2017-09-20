package it.uniupo.gallomunoz.reti2lab;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;

import org.eclipse.jetty.websocket.api.Session;
import org.eclipse.jetty.websocket.api.annotations.OnWebSocketClose;
import org.eclipse.jetty.websocket.api.annotations.OnWebSocketConnect;
import org.eclipse.jetty.websocket.api.annotations.OnWebSocketMessage;
import org.eclipse.jetty.websocket.api.annotations.WebSocket;
import org.json.JSONObject;

@WebSocket(maxTextMessageSize = 64 * 1024)
public class MyWSUtil {
	
	private final CountDownLatch closeLatch;
	@SuppressWarnings("unused")
	private Session session;
	private SubjectTable st;

	public MyWSUtil(SubjectTable st) {
		this.closeLatch = new CountDownLatch(1);
		System.out.println("Client running...");
		this.st = st;
	}

	public boolean awaitClose(int duration, TimeUnit unit) throws InterruptedException {
		return this.closeLatch.await(duration, unit);
	}

	@OnWebSocketClose
	public void onClose(int statusCode, String reason) {
		System.out.printf("Connection closed: %d - %s%n", statusCode, reason);
		this.session = null;
		this.closeLatch.countDown(); // trigger latch
	}

	@OnWebSocketConnect
	public void onConnect(Session session) {
		System.out.printf("Connected: %s%n", session);
		this.session = session;
		try {
			Future<Void> fut;
			fut = session.getRemote().sendStringByFuture("");
			fut.get(2, TimeUnit.SECONDS); // wait for send to complete.

			fut = session.getRemote().sendStringByFuture("");
			fut.get(2, TimeUnit.SECONDS); // wait for send to complete.
		} catch (Throwable t) {
			t.printStackTrace();
		}
	}
	
	@OnWebSocketMessage
	public void onMessage(String msg) {
		System.out.println("[" + Thread.currentThread().getName() + "]" + " received: " + msg);
		
		//preparo un messaggio e lo notifico alla subject table
		JSONObject msgJson =  new JSONObject(msg); 
		
		switch((String) msgJson.get("name")){
			case "SE_Pir1":
				notifyMessage("/sensors/s1", msg);
				break;
			
			case "SE_Pir2":
				notifyMessage("/sensors/s2", msg);
				break;
			
			case "SE_Pir3":
				notifyMessage("/sensors/s3", msg);
				break;
			
			case "SO_Lampeggiatore":
				notifyMessage("/attuators/lampeggiatore", msg);
				break;
				
			case "SO_Sirena":
				notifyMessage("/attuators/sirena", msg);
				break;
				
			default:
				//nulla
				break;
		}
	}
	
	private void notifyMessage(String uri, String msg){
		Message m = new Message(uri, "PUT", msg);
		try {
			st.MessageNotify(m);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}
