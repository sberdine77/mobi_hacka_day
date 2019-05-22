package server.ws;
import java.io.IOException;

import javax.websocket.Session;

import com.espertech.esper.common.client.EventBean;
import com.espertech.esper.runtime.client.EPRuntime;
import com.espertech.esper.runtime.client.EPStatement;
import com.espertech.esper.runtime.client.UpdateListener;

public class EsperListener implements UpdateListener {
	
	private Session s;
	
	public EsperListener (Session s) {
		this.s = s;
	}
	
	@Override
	public void update(EventBean[] newEvents, EventBean[] oldEvents, EPStatement statment, EPRuntime runtime) {
		System.out.println(statment.getName() + " events " + (newEvents == null ? " null " : newEvents.length));
		String latitude = (String) newEvents[0].get("latitude");
		String longitude = (String) newEvents[0].get("longitude");
		String location = latitude + ", " + longitude;
		try {
			s.getBasicRemote().sendText(location);
		}  catch (IOException e) {
			e.printStackTrace();
		}
	}
}