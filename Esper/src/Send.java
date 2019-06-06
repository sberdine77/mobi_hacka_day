import com.rabbitmq.client.ConnectionFactory;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.Channel;

public class Send {
	
	private final static String QUEUE_NAME = "hello";
	public static void main(String[] argv) throws Exception {
		ConnectionFactory factory = new ConnectionFactory();
		factory.setHost("localhost");
		try (Connection connection = factory.newConnection();
			Channel channel = connection.createChannel()) {
			channel.queueDeclare(QUEUE_NAME, false, false, false, null);
			String message = "{\"unidade\": \"3333\", \"empresa\": \"Empresa_13\", \"matricula\": \"3333\", \"instante\": \"2018-01-01 23:13:42.740\", \"latitude\": -8.001390563622275, \"longetude\": -34.87022658976693}\n";
			channel.basicPublish("", QUEUE_NAME, null, message.getBytes());
			System.out.println(" [x] Sent '" + message + "'");
		}
			
	}
}
