/**
 *  js che genera l'interfaccia dell'antifurto, cioè, le varie zone e le opzioni per allarmarle/disallarmarle
 */


//il messaggio con get che permette di costruire l'elenco delle zone va eseguito solo una volta
var isFirstGet = true;

function start(){
	
	// Create a client instance
	client = new Paho.MQTT.Client("193.206.55.23",
									Number(9001), 
									"AntifurtoClientID" + randomString(16, '0123456789abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ'));

	// set callback handlers
	client.onConnectionLost = onConnectionLost;
	client.onMessageArrived = onMessageArrived;

	// connect the client
	client.connect({onSuccess:onConnect});
}


//called when the client connects
function onConnect() {
  // Once a connection has been made, make a subscription and send a message.
  console.log("onConnect");
  client.subscribe("/GalloMunoz/Antifurto/AgenteOutput/#");
  //message = new Paho.MQTT.Message("{\"accensione\":\"true\"}");
  //message.destinationName = "/GalloMunoz/Antifurto/AgenteInput/attuators/lampeggiatore";
  //client.send(message);
  
  /*
   * per prima cosa ci serve un elenco delle zone
   */
  message = new Paho.MQTT.Message("{\"method\":\"GET\"}");
  message.destinationName = "/GalloMunoz/Antifurto/AgenteInput/zones";
  client.send(message);
  
  
}

// called when the client loses its connection
function onConnectionLost(responseObject) {
  if (responseObject.errorCode !== 0) {
    console.log("onConnectionLost:"+responseObject.errorMessage);
  }
}

// called when a message arrives
function onMessageArrived(message) {
  //debug
  console.log("onMessageArrived:"+message.payloadString);
  
  //otteniamo un oggetto JSON dal corpo della messaggio
  var bodyResp = JSON.parse(message.payloadString);
  
  //se contiene il campo "zones" allora devo costruire le zone sulla pagina
  if(isFirstGet && bodyResp.hasOwnProperty("zones")){
	  isFirstGet = false;
	  for(var key in bodyResp.zones){ //ciclo l'array dei json
  		if(bodyResp.zones.hasOwnProperty(key)){
  			for(var jsonKey in bodyResp.zones[key]){ //ciclo il singolo json
  				document.getElementById("pagina").insertAdjacentHTML('beforeend',
  						jsonKey + " = " + "<span id=\"button_" + jsonKey + "\">" + "<img class=\"icona\" src=\"img/allarmato_" + bodyResp.zones[key][jsonKey] + ".gif\"></img>" + "</span>" +
  						"<button type=\"button\" onclick=\"zoneOn('" + jsonKey + "')\">allarma</button>" +
  						"<button type=\"button\" onclick=\"zoneOff('" + jsonKey +"')\">disallarma</button>" +"<br><hr>"
  						);
  			}
  		}
  	}
  }
  if(bodyResp.hasOwnProperty("risorsa")){
	  document.getElementById("button_" + bodyResp.risorsa).innerHTML = "<img class=\"icona\" src=\"img/allarmato_" + bodyResp.allarmato + ".gif\"></img>";
  }
  if(bodyResp.hasOwnProperty("name")){
	  if(bodyResp.name.valueOf() == new String('SO_Lampeggiatore').valueOf()){
		  document.getElementById("SO_Lampeggiatore").src = "img/lampeggiatore_" + bodyResp.status + ".png" ;
	  }
	  if(bodyResp.name.valueOf() == new String('SO_Sirena').valueOf()){
		  document.getElementById("SO_Sirena").src = "img/sirena_" + bodyResp.status + ".gif" ;
	  }
	  if(bodyResp.name.valueOf() == new String('SE_Pir1').valueOf()){
		  document.getElementById("SE_Pir1").src = "img/SE_Pir" + bodyResp.status + ".png" ;
	  }
	  if(bodyResp.name.valueOf() == new String('SE_Pir2').valueOf()){
		  document.getElementById("SE_Pir2").src = "img/SE_Pir" + bodyResp.status + ".png" ;
	  }
	  if(bodyResp.name.valueOf() == new String('SE_Pir3').valueOf()){
		  document.getElementById("SE_Pir3").src = "img/SE_Pir" + bodyResp.status + ".png" ;
	  }
  }
}



function zoneOn(risorsa) {
	/*
	 * mandiamo messaggio mqtt per accendere la risorsa
	 */
	message = new Paho.MQTT.Message("{\"allarmato\":\"on\",\"method\":\"PUT\"}");
	message.destinationName = "/GalloMunoz/Antifurto/AgenteInput" + risorsa;
	client.send(message);
}

function zoneOff(risorsa) {
	/*
	 * mandiamo messaggio mqtt per spegnere la risorsa
	 */
	message = new Paho.MQTT.Message("{\"allarmato\":\"off\",\"method\":\"PUT\"}");
	message.destinationName = "/GalloMunoz/Antifurto/AgenteInput" + risorsa;
	client.send(message);
	}

function allarmaAll(){
	/*
	 * mandiamo messaggio mqtt per allarmare tutte le zone
	 */
	message = new Paho.MQTT.Message("{\"allarmato\":\"on\",\"method\":\"PUT\"}");
	message.destinationName = "/GalloMunoz/Antifurto/AgenteInput/zones";
	client.send(message);
}

function disallarmaAll(){
	/*
	 * mandiamo messaggio mqtt per disallarmare tutte le zone
	 */
	message = new Paho.MQTT.Message("{\"allarmato\":\"off\",\"method\":\"PUT\"}");
	message.destinationName = "/GalloMunoz/Antifurto/AgenteInput/zones";
	client.send(message);
}

function randomString(length, chars) {
    var result = '';
    for (var i = length; i > 0; --i) result += chars[Math.floor(Math.random() * chars.length)];
    return result;
}