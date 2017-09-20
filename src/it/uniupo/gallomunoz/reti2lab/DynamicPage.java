package it.uniupo.gallomunoz.reti2lab;

//import java.util.ArrayList;
import java.util.HashMap;

public class DynamicPage {
	private HashMap<String, ZoneStatus> dp;
	
	public DynamicPage(){
		dp = new HashMap<String, ZoneStatus>();
	}
	
	public synchronized void publish(String zone, String status){
		if(dp.put(zone, new ZoneStatus(zone, status)) != null){
			//ok
		}
		else{
			dp.remove(zone);
			try {
				throw new Exception("Zona non esistente");
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}
	
	public synchronized void createZone(ZoneStatus zone){
		dp.put(zone.zona, zone);
	}
	
	public synchronized ZoneStatus getZoneStatus(int zone){
		ZoneStatus zs = dp.get(zone);
		if(zs != null ){
			//ok
		}
		else{
			try {
				throw new Exception("Zona non esistente");
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		
		return new ZoneStatus(zs.zona, zs.status);
	}
	
	@SuppressWarnings("unchecked")
	public synchronized HashMap<String, ZoneStatus> getDpHash(){
		return (HashMap<String, ZoneStatus>) dp.clone();
	} 
	
	
}
