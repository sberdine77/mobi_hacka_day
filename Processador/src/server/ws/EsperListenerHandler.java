package server.ws;

import java.util.HashMap;
import java.util.Map;
import java.util.Vector;

import javax.websocket.Session;

import com.espertech.esper.runtime.client.EPStatement;
import com.espertech.esper.runtime.client.UpdateListener;

public class EsperListenerHandler {
	
	private Map<Session, EsperListener> listeners;
	private Vector<EPStatement> statements;
	
	public EsperListenerHandler () {
		this.statements = new Vector<EPStatement>();
		this.listeners = new HashMap<Session, EsperListener>();
	}
	
	public EPStatement addListenerToStatement (Session s, EPStatement statement) {
		EsperListener listener = new EsperListener(s);
		if (this.isActiveSession(s)) {
			statement = this.removeListenerFromSession(statement, s);
		}
		
		this.listeners.put(s, listener);
		
		statement.addListener(listener);
		/*if (this.isExistingStatement(statement)) {
			int i = this.statements.indexOf(statement);
			this.statements.remove(i);
			this.statements.add(statement);
		} else {
			this.statements.add(statement);
		}*/
		this.listeners.put(s, listener);
		return statement;
	}
	
	public boolean isActiveSession (Session s) {
		if (this.listeners.containsKey(s)) {
			return true;
		}
		return false;
	}
	
	public boolean isExistingStatement (EPStatement statement) {
		if (this.statements.contains(statement)) {
			return true;
		}
		return false;
	}
	
	private EPStatement removeListenerFromSession (EPStatement statement, Session s) {
		UpdateListener localListener = (UpdateListener) this.listeners.get(s);
		int i = this.statements.indexOf(statement);
		System.out.println("AQUI NA LISTENER HANDLER BB " + Integer.toString(i));
		EPStatement localStatement = this.statements.get(i);
		statement.removeListener((UpdateListener) localListener);
		localStatement.removeListener((UpdateListener) localListener);
		this.statements.remove(i);
		this.statements.add(statement);
		return statement;
	}
	
	
	
}
