public class Log {
	private int id;
	private String empresa;
	private String matricula;
	private String timeStamp;
	private String latitude;
	private String longitude;
	
	public Log() {}
	
	public Log(int id, String empresa, String matricula, String timeStamp, String latitude, String longitude) {
		this.id = id;
		this.empresa = empresa;
		this.matricula = matricula;
		this.timeStamp = timeStamp;
		this.latitude = latitude;
		this.longitude = longitude;
	}
	
	public double id() {
		return id;
	}
	public String empresa() {
		return empresa;
	}
	
	public String matricula() {
		return matricula;
	}
	
	public String timeStamp() {
		return timeStamp;
	}
	
	public String latitude() {
		return latitude;
	}
	
	public String longitude() {
		return longitude;
	}
	
	public void setId(int id) {
		this.id = id;
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
		return "Id: " + String.valueOf(id) + ", Empresa: " + empresa + ", Matrícula: " + matricula + ", TimeStamp: " + timeStamp + ", Latitude: " + latitude + ", Longitude: " + longitude;
	}
}
