import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URI;
import java.util.concurrent.TimeUnit;

import org.eclipse.jetty.websocket.client.ClientUpgradeRequest;
import org.eclipse.jetty.websocket.client.WebSocketClient;

public class Main {

	private static String wsUri;
	
	public static void main(String[] args) {
		setVars();
		WebSocketClient client = new WebSocketClient();
		WSUtil socket = new WSUtil();
		try {
			client.start();
			URI echoUri = new URI(wsUri);
			ClientUpgradeRequest request = new ClientUpgradeRequest();
			client.connect(socket, echoUri, request);
			System.out.printf("Connecting to : %s%n", echoUri);

			// wait for closed socket connection.
			socket.awaitClose(Integer.MAX_VALUE, TimeUnit.DAYS);
		} catch (Throwable t) {
			t.printStackTrace();
		} finally {
			try {
				client.stop();
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}
	
	/**
	 * Reads the configuration file and sets the parameters for the
	 * connection.
	 */
	private synchronized static void setVars() {
		File file = new File("./cfg.txt");
		if (file.exists() && !file.isDirectory()) {
			BufferedReader reader;
			try {
				reader = new BufferedReader(new InputStreamReader(new FileInputStream(file)));
				String line = new String();
				while ((line = reader.readLine()) != null) {
					if (!line.startsWith("#") && !line.isEmpty()) {
							wsUri = line;
					}
				}
				reader.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
}
}
