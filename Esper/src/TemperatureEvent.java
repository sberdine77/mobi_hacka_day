public class TemperatureEvent {
	private double leitura;
	
	public TemperatureEvent() {}
	
	public TemperatureEvent(double leitura) {
		this.leitura = leitura;
	}
	
	public double getLeitura() {
		return leitura;
	}
	
	public void setLeitura(double leitura) {
		this.leitura = leitura;
	}
	
	public String toString() {
		return "Temperature " + leitura;
	}
}