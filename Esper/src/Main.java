import com.espertech.esper.common.client.EPCompiled;
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

public class Main {
	public static void main (String args[]) {
		
		EPCompiler compiler = EPCompilerProvider.getCompiler();
		Configuration configuration = new Configuration();
		configuration.getCommon().addEventType(TemperatureEvent.class);

		CompilerArguments cargs = new CompilerArguments(configuration);
		EPCompiled epCompiled;
		try {
			epCompiled = compiler.compile("@name('maior-que-25') select * from TemperatureEvent where leitura > 25 ", cargs);
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
		
		EPStatement statement = runtime.getDeploymentService().getStatement(deployment.getDeploymentId(), "maior-que-25");
		
		statement.addListener((newData, oldData, mystatement, myruntime) -> {
			System.out.println(mystatement.getName() + " events " + (newData == null ? " null " : newData.length));
			System.out.println(newData[0].get("leitura"));
		});
		
		new Thread(new Runnable() {
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
		}).start();
	}
}
