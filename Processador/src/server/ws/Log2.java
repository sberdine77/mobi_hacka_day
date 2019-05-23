package server.ws;
public class Log2 {
	private int unidade;
	private String empresa;
	private String matricula;
	private String timeStamp;
	private String latitude;
	private String longitude;
	
	public Log2() {}
	
	public Log2(int unidade, String empresa, String matricula, String timeStamp, String latitude, String longitude) {
		this.unidade = unidade;
		this.empresa = empresa;
		this.matricula = matricula;
		this.timeStamp = timeStamp;
		this.latitude = latitude;
		this.longitude = longitude;
	}
	
	public int getUnidade() {
		return unidade;
	}
	public String getEmpresa() {
		return empresa;
	}
	
	public String getMatricula() {
		return matricula;
	}
	
	public String getTimeStamp() {
		return timeStamp;
	}
	
	public String getLatitude() {
		return latitude;
	}
	
	public String getLongitude() {
		return longitude;
	}
	
	public void setUnidade(int unidade) {
		this.unidade = unidade;
	}
	
	public void setEmpresa(String empresa) {
		this.empresa = empresa;
	}
	
	public void setMatricula(String matricula) {
		this.matricula = matricula;
	}
	
	public void setTimeStamp(String timeStamp) {
		this.timeStamp = timeStamp;
	}
	
	public void setLatitude(String latitude) {
		this.latitude = latitude;
	}
	
	public void setLongitude(String longitude) {
		this.longitude = longitude;
	}
	
	public String toString() {
		return "Unidade: " + String.valueOf(unidade) + ", Empresa: " + empresa + ", Matrícula: " + matricula + ", TimeStamp: " + timeStamp + ", Latitude: " + latitude + ", Longitude: " + longitude;
	}
}
