import com.espertech.esper.common.client.EPCompiled;
import com.espertech.esper.common.client.EventBean;
import com.espertech.esper.common.client.configuration.Configuration;
import com.espertech.esper.compiler.client.CompilerArguments;
import com.espertech.esper.compiler.client.EPCompileException;
import com.espertech.esper.compiler.client.EPCompiler;
import com.espertech.esper.compiler.client.EPCompilerProvider;
import com.espertech.esper.runtime.client.EPDeployException;
import com.espertech.esper.runtime.client.EPDeployment;
import com.espertech.esper.runtime.client.EPRuntime;
import com.espertech.esper.runtime.client.EPRuntimeProvider;
import com.espertech.esper.runtime.client.EPStatement;
import com.espertech.esper.runtime.client.UpdateListener;
import com.espertech.esper.runtime.client.scopetest.SupportUpdateListener;

import com.google.gson.Gson;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;
import com.rabbitmq.client.DeliverCallback;

import java.util.*;

import javax.websocket.OnClose;
import javax.websocket.OnMessage;
import javax.websocket.OnOpen;
import javax.websocket.Session;
import javax.websocket.server.PathParam;
import javax.websocket.server.ServerEndpoint;

import java.io.IOException;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.TimeoutException;

@ServerEndpoint("/endpoint")
public class Main {
	
	private static List<UpdateListener> listeners = new ArrayList<UpdateListener>();
	private static Set<Session> peers = Collections.synchronizedSet(new HashSet());
	private static EPCompiler compiler = EPCompilerProvider.getCompiler();
	private static Configuration configuration = new Configuration();
	
	private final static String QUEUE_NAME = "hello";
	
	@OnMessage
    public void recebeMensagem(String mensagem, Session session, @PathParam("client-id") String clientId) throws NumberFormatException, IOException, TimeoutException {
		String[] array;
		array = mensagem.split("-");
		if (array[0].equals("getLogFromBusId")) {
			getLogFromBusId(Integer.parseInt(array[1]), session);
		}
    }
	
	@OnOpen
	public void onOpen(Session session, @PathParam("client-id") String clientId) {
		System.out.println("mediator: opened websocket channel for client " + clientId);
	}
	
	@OnClose
	public void onClose(Session session, @PathParam("client-id") String clientId) {
		System.out.println("mediator: closed websocket channel for client " + clientId);
		peers.remove(session);
	}
	
	
	public static void main (String args[]) throws IOException, TimeoutException {
		
		configuration.getCommon().addEventType(Log.class);
		
		EPRuntime runtime = EPRuntimeProvider.getDefaultRuntime(configuration);
		
		ConnectionFactory factory = new ConnectionFactory();
	    factory.setHost("localhost");
	    Connection connection = factory.newConnection();
	    Channel channel = connection.createChannel();

	    channel.queueDeclare(QUEUE_NAME, false, false, false, null);
	    System.out.println(" [*] Waiting for messages. To exit press CTRL+C");
	    
	    DeliverCallback deliverCallback = (consumerTag, delivery) -> {
	        String message = new String(delivery.getBody(), "UTF-8");
	        System.out.println(" [x] Received '" + message + "'");
	        Gson g = new Gson();
	        Log log = g.fromJson(message, Log.class);
	        runtime.getEventService().sendEventBean(log, "Log");
	        
	    };
	    channel.basicConsume(QUEUE_NAME, true, deliverCallback, consumerTag -> { });
	    
	    //Session s = null;
	    
	    //getLogFromBusId(3333, s);
		
		/*new Thread(new Runnable() {
			public void run() {
				while (true) {
					double leituraAleatoria = Math.random() * 50;
					TemperatureEvent event = new TemperatureEvent(leituraAleatoria);
					System.out.println("Publicando " + event);
					runtime.getEventService().sendEventBean(event, "TemperatureEvent");
					try {
						Thread.sleep(1000);
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
				}
			}
		}).start();*/
	}
	
	public static void getLogFromBusId (int id, Session s) throws IOException, TimeoutException {
		
		configuration.getCommon().addEventType(Log.class);

		CompilerArguments cargs = new CompilerArguments(configuration);
		EPCompiled epCompiled;
		
		try {
			String str = "@name('getLogFromBusId') select * from Log where id =  " + Integer.toString(id);
			epCompiled = compiler.compile(str, cargs);
		} catch (EPCompileException ex) {
			throw new RuntimeException(ex);
		}
		EPRuntime runtime = EPRuntimeProvider.getDefaultRuntime(configuration);
		EPDeployment deployment;
		try {
			deployment = runtime.getDeploymentService().deploy(epCompiled);
		} catch (EPDeployException ex) {
			throw new RuntimeException(ex);
		}
		
		EPStatement statement = runtime.getDeploymentService().getStatement(deployment.getDeploymentId(), "getLogFromBusId");
		
		EsperListener listener = new EsperListener(s);
		//statement.addListener((UpdateListener) listener);
		statement.addListener(listener);
		peers.add(s);
		//listeners.add((UpdateListener) listener);
		
	}
	
	public void getLogFromAllBuses () {
		
	}
	
	public void getLogFromSomeBusIds (int[] ids) {
		
	}
	
}
