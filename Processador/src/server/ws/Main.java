	
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

//Marcador websocket
@ServerEndpoint("/websocketendpoint")
public class Main {
	
	/*Possíveis variáveis para controle de exclusão de listeners do Esper e websocket
	 * sessions. Idealmente existiria uma ou duas classes hendlers. Porém não vi 
	 * prioridade imediata em sequer fazer um controle mais simples*/
	private static List<UpdateListener> listeners = new ArrayList<UpdateListener>();
	private static Set<Session> peers = Collections.synchronizedSet(new HashSet());
	
	private static EPCompiler compiler;
	private static Configuration configuration;
	
	private static EsperHandler espHandler;
	
	private final static String QUEUE_NAME = "hello";
    
	//Marcador websocket
    @OnOpen
    public void onOpen() {
		System.out.println("Server opening...");
		try {
			Main.main(null);
		} catch (IOException | TimeoutException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
    
    /*Se for utilizar a variável peers para controle de sessões, receber aqui a session
     * como parâmetro para poder excluíla do peers no encerramento de uma conexão.*/
    //Marcador websocket
    @OnClose
    public void onClose() {
		System.out.println("Closing server...");
	}
    
    /*Pronto: Ao receber uma mensagem via websocket, dou split e verifico 
     * se a pessoa está requisitando o id de um ônibus específico. 
     * Se sim, chamo uma função que irá chamar dar um select com este ID no stream
     * que estará entrando no Esper*/
    @OnMessage //Marcador websocket
    public void recebeMensagem(String mensagem, Session session) throws NumberFormatException, IOException, TimeoutException {
		String[] array;
		array = mensagem.split("-");
		if (array[0].equals("getLogFromBusId")) {
			System.out.println("Tá prontin pra chamar a função..." + array[1]);
			getLogFromBusId(array[1], session);
		}
    }
 
    //Marcador websocket
    @OnError
    public void onError(Throwable e){
        e.printStackTrace();
    }
    
public static void main (String args[]) throws IOException, TimeoutException {
		
	System.out.println("AQUI NA MAIN BB");
	
	configuration = new Configuration();
	
		configuration.getCommon().addEventType("Log2", Log2.class);
		compiler = EPCompilerProvider.getCompiler();
		
		
		EPRuntime runtime = EPRuntimeProvider.getDefaultRuntime(configuration);
		CompilerArguments cargs = new CompilerArguments(configuration);
		
		espHandler = new EsperHandler(configuration, runtime, cargs, compiler);
		
		//RabbitMQ_start
		/*Pronto: já está recebendo de uma fila o formato do log (Pode ser visto
		 * um exemplo na classe Send.java). Converte de JSON para o tipo de dados
		 * que criei (Log2) e (Parte do Esper agora..) envia o evento para tratamento
		 * do Esper*/
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
	    //RabbitMq_end
		
	    
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
/*Pronto: função que é chamada quando o server recebe uma requisição válida para
 * retornar o log de um ônibus com ID específico.
 * Parâmetros: String iD -> ID (unidade) do ônibus
 * Session s -> referência para uma websocket session para posterior comunicação
 * com quem chamou a função.*/
public static void getLogFromBusId (String id, Session s) throws IOException, TimeoutException {
	String str = "@name('getLogFromBusId') Select * from Log2 where unidade = " + id + " ";
	espHandler.getLogFromBusIdWithSql(str , s);
	System.out.println("CHAMOU BB");
	
}

//A fazer, mas não é mais necessário
public void getLogFromAllBuses () {
	
}

/*A fazer, similar ao getLogFromBusId, porém criará vários statments interando sobre a 
 * entrada cada um com seu respectivo listener,*/
public void getLogFromSomeBusIds (String[] id, Session s) {
	
}
 
}