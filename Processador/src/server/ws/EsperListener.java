package server.ws;
import java.io.IOException;
import java.util.List;

import javax.websocket.Session;

import com.espertech.esper.common.client.EventBean;
import com.espertech.esper.runtime.client.EPRuntime;
import com.espertech.esper.runtime.client.EPStatement;
import com.espertech.esper.runtime.client.UpdateListener;

/*Rodando como java application, este listener é chamado. Ao que tudo indica,
 * rodando a aplicação no Tomcat ele nunca é chamado. Isto precisa ser corrigido
 * pois é a partir deste listener que precisamos enviar o log filtrado para o usuário*/
public class EsperListener implements UpdateListener {
	
	private Session s;
	private EPStatement statement;
	
	/*Construtor personalizado para ter acesso à session do user que o chamou
	 * para enviar o resultado já tratado (Log de um bus es[ecífico)*/
	public EsperListener (Session s, EPStatement statement) {
		this.s = s;
		this.statement = statement;
	}
	
	/*newEvents é o array de eventos que atendem ao select do statement a qual este
	 * listener está atrelado. Já é esperado um event bean no formato que eu setei (Log2.class)*/
	@Override
	public void update(EventBean[] newEvents, EventBean[] oldEvents, EPStatement statment, EPRuntime runtime) {
		System.out.println(statment.getName() + " events " + (newEvents == null ? " null " : newEvents.length));
		Integer unidade = (Integer) newEvents[0].get("unidade");
		String latitude = (String) newEvents[0].get("latitude");
		String longitude = (String) newEvents[0].get("longitude");
		String location = Integer.toString(unidade.intValue()) + "," + latitude + "," + longitude;
		/*Aqui faço a real tentativa de envio do log filtrado de volta para a mesma
		 * websocket session que chamou o listener. Como o websocket endpoint roda no Tomcat
		 * e no Tomcat este listener aparentemente não é chamado, este envio não acontece.*/
		try {
			s.getBasicRemote().sendText(location);
		}  catch (IOException e) {
			e.printStackTrace();
		}
	}
}