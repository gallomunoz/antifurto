package it.uniupo.gallomunoz.reti2lab;

import java.util.ArrayList;


/**
 * 
 * @author 10031370 10032452
 * 
 * questa classe rappresenta un soggetto
 *
 */
public class Subject {
	public String name;
	public ArrayList<MessageQueue> subscribers;
	public ArrayList<Subject> sons;
	
	public Subject(String name){
		this.name = name;
		subscribers = new ArrayList<MessageQueue>();
		sons = new ArrayList<Subject>();
	}
}
