package server.ws;

import javax.websocket.Session;

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

public class EsperHandler {
	
	private EPCompiler compiler;
	private Configuration configuration;
	private EPRuntime runtime;
	private CompilerArguments cargs;
	private EPCompiled epCompiled;
	private EsperListenerHandler listenersHandler;
	
	private EPStatement statement;
	
	public EsperHandler (Configuration configuration, EPRuntime runtime, CompilerArguments cargs, EPCompiler compiler) {
		this.compiler = compiler;
		this.configuration  = configuration;
		this.runtime = runtime;
		this.cargs = cargs;
		this.listenersHandler = new EsperListenerHandler();
	}
	
	public void getLogFromBusIdWithSql (String sqlCommand, Session s) {
		
		try {
			epCompiled = compiler.compile(sqlCommand, cargs);
		} catch (EPCompileException ex) {
			throw new RuntimeException(ex);
		}
		EPDeployment deployment;
		try {
			deployment = runtime.getDeploymentService().deploy(epCompiled);
		} catch (EPDeployException ex) {
			throw new RuntimeException(ex);
		}
		this.statement = runtime.getDeploymentService().getStatement(deployment.getDeploymentId(), "getLogFromBusId");
		
		
		listenersHandler.addListenerToStatement(s, this.statement);
		
	}
	
}
