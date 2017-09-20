import java.util.concurrent.CountDownLatch;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;

import org.eclipse.jetty.websocket.api.Session;
import org.eclipse.jetty.websocket.api.annotations.OnWebSocketClose;
import org.eclipse.jetty.websocket.api.annotations.OnWebSocketConnect;
import org.eclipse.jetty.websocket.api.annotations.OnWebSocketMessage;
import org.eclipse.jetty.websocket.api.annotations.WebSocket;

@WebSocket(maxTextMessageSize = 64 * 1024)
public class WSUtil {
	
	private final CountDownLatch closeLatch;
	private Session session;

	public WSUtil() {
		this.closeLatch = new CountDownLatch(1);
		System.out.println("Client running...");
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
		System.out.println("received: " + msg);
	}
}
