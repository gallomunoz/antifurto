package it.uniupo.gallomunoz.reti2lab;

import java.util.TimerTask;

public class MessageTimeTask extends TimerTask
{
	private SubjectTable st;
	private Message m;
	
	public MessageTimeTask(SubjectTable st, Message m){
		this.st = st;
		this.m = m;
	}

    public void run()
    {
		//facciamo la notify verso l'attuatore
		try {
			st.MessageNotify(m);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
    	
    	
    }
}