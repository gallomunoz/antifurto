package it.uniupo.gallomunoz.reti2lab;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URI;

import org.eclipse.paho.client.mqttv3.IMqttDeliveryToken;
import org.eclipse.paho.client.mqttv3.MqttCallback;
import org.eclipse.paho.client.mqttv3.MqttClient;
import org.eclipse.paho.client.mqttv3.MqttConnectOptions;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.eclipse.paho.client.mqttv3.MqttMessage;
import org.eclipse.paho.client.mqttv3.MqttPersistenceException;
import org.eclipse.paho.client.mqttv3.MqttSecurityException;
import org.eclipse.paho.client.mqttv3.persist.MemoryPersistence;
import org.json.JSONObject;

public class MyMqttClientThread implements Runnable, MqttCallback{
	
	private String topicInput   = "";
	private String topicOutput  = "";
    private String content      = "";
    private int qos             = 2;
    private String broker       = "";
    private String clientId     = "";
    private MemoryPersistence persistence = new MemoryPersistence();
	private SubjectTable st = null;
    private MessageQueue mq;
    private Message m;
	private MqttClient client;
	private MqttMessage message;
	private String MttqMessageMethod;
    
    public MyMqttClientThread(SubjectTable st){
    	this.st = st;
    	getMqttCfg();
    }
    
	@Override
	public void run() {
		
        try {
        	client = new MqttClient(broker, clientId, persistence);
            MqttConnectOptions connOpts = new MqttConnectOptions();
            connOpts.setCleanSession(true);
            client.setCallback(new MyMqttClientThread(st));
            System.out.println("Connessione al broker: "+broker);
			client.connect(connOpts);
			System.out.println("...Connesso");
			
			//creo topic sul broker
			System.out.println("Publishing message: "+ content);
	        message = new MqttMessage(content.getBytes());
	        message.setQos(qos);
	        client.publish(topicOutput, message);
	        client.publish(topicInput, message);
	        System.out.println("Messaggio pubblicato");
	        
	        //mi metto in ascolto, quindi devo sottoscrivermi al topic
	        //ed a tutti i sub topic con la wildcard #
	        client.subscribe(topicInput + "/#");
	        
	        
			
		} catch (MqttSecurityException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (MqttException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
        
        /*
         * mi sottoscrivo a / nella subjectable del nostro agente
         * quando ricevo un messaggio lo inoltro al broker mqtt
         */
        
        mq = new MessageQueue(100);
        try {
			st.subscribe("/", mq);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
        
        while(true){
        	m = mq.receive();
        	message = new MqttMessage(m.getBodyAsString().getBytes());
        	try {
				client.publish(topicOutput + m.getUri(), message);
				System.out.println("MQTT: publish sul topic " + topicOutput + m.getUri() +
									" del messaggio" + m.getBodyAsString());
			} catch (MqttPersistenceException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (MqttException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
        }
        
	}
	
	
	/**
	 * metodi callback mqqt
	 */
	
	public void connectionLost(Throwable arg0) {
        System.err.println("[" + Thread.currentThread().getName() + "]" + "connection lost");

    }

    public void deliveryComplete(IMqttDeliveryToken arg0) {
        System.err.println("[" + Thread.currentThread().getName() + "]" + "delivery complete");
    }

    public void messageArrived(String topic, MqttMessage message) throws Exception {
        System.out.println("[ " + Thread.currentThread().getName() + " ]"  + "topic: " + topic);
        System.out.println("[ " + Thread.currentThread().getName() + " ]" + "message: " + new String(message.getPayload()));
        
        /*
         * quando arriva un messaggio da mqtt dobbiamo assemblarlo in un nostro messaggio
         * della classe Message e mandarlo alla subject table, col giusto metodo
         * i messaggi che arrivano da mqtt devono  possedere il campo "method", e quindi
         * scegliere il giusto metodo REST
         */
        
        JSONObject payloadMqtt= new JSONObject(new String(message.getPayload()));
        MttqMessageMethod = payloadMqtt.getString("method");
        
        //a questo punto non ci serve più il campo method nel json
        payloadMqtt.remove("method");
        
        
        
        Message m = new Message(("/" + URI.create(topicInput).relativize(URI.create(topic))).toString(),
        						MttqMessageMethod,
        						payloadMqtt.toString());
        st.MessageNotify(m);
    }
	
	
	
	
	
	
	
	
	
	
	
	
	/**
	 * legge i dati di configurazione dal file mqttBroker.cfg utili per collegarsi al brocker
	 */
	
	public void getMqttCfg(){
		File file = new File("./mqttBroker.cfg");
		if (file.exists() && !file.isDirectory()) {
			BufferedReader reader = null;
			try {
				reader = new BufferedReader(new InputStreamReader(new FileInputStream(file)));
			} catch (FileNotFoundException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			for(int i = 0; i<3; i++){
				switch(i){
					case 0:
					try {
						topicInput = reader.readLine();
					} catch (IOException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
						break;
					case 1:
						try {
							topicOutput = reader.readLine();
						} catch (IOException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
							break;
					case 2:
					try {
						broker = reader.readLine();
					} catch (IOException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
						break;
					case 3:
					try {
						clientId = reader.readLine();
					} catch (IOException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
						break;
					default:
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
