	
package server.ws;
 
import java.io.IOException;
import java.util.concurrent.TimeoutException;

import javax.websocket.OnClose;
import javax.websocket.OnError;
import javax.websocket.OnMessage;
import javax.websocket.OnOpen;
import javax.websocket.Session;
import javax.websocket.server.PathParam;
import javax.websocket.server.ServerEndpoint;

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

import com.google.gson.Gson;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;
import com.rabbitmq.client.DeliverCallback;

import java.util.*;

 
@ServerEndpoint("/websocketendpoint")
public class Main {
	
	private static List<UpdateListener> listeners = new ArrayList<UpdateListener>();
	private static Set<Session> peers = Collections.synchronizedSet(new HashSet());
	private static EPCompiler compiler = EPCompilerProvider.getCompiler();
	private static Configuration configuration = new Configuration();
	
	private final static String QUEUE_NAME = "hello";
     
    @OnOpen
    public void onOpen() {
		System.out.println("Server opening...");
	}
     
    @OnClose
    public void onClose() {
		System.out.println("Closing server...");
	}
     
    @OnMessage
    public void recebeMensagem(String mensagem, Session session) throws NumberFormatException, IOException, TimeoutException {
		String[] array;
		array = mensagem.split("-");
		if (array[0].equals("getLogFromBusId")) {
			System.out.println("Tá prontin pra chamar a função..." + array[1]);
			getLogFromBusId(array[1], session);
		}
    }
 
    @OnError
    public void onError(Throwable e){
        e.printStackTrace();
    }
    
public static void main (String args[]) throws IOException, TimeoutException {
		
		configuration.getCommon().addEventType("Log2", Log2.class);
		
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
	        Log2 log2 = g.fromJson(message, Log2.class);
	        runtime.getEventService().sendEventBean(log2, "Log2");
	        
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

public static void getLogFromBusId (String id, Session s) throws IOException, TimeoutException {
	
	configuration.getCommon().addEventType(Log2.class);

	CompilerArguments cargs = new CompilerArguments(configuration);
	EPCompiled epCompiled;
	//configuration.getEPAdministrator().getConfiguration().addEventTypeAlias("Log", Log.class); 
	try {
		String str = "@name('getLogFromBusId') select * from Log2 where unidade =  " + id + " ";
		System.out.println(str);
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