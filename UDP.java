//classe que armazena os dados do UDP na camada de transporte
public class UDP extends TransportLayer{

	private String protocol;
	private int source_port;
	private int destination_port;
	private int length;
	
	//Construtor
	public UDP(int source, int destination)
	{
		this.source_port = source;
		this.destination_port = destination;
	}
	
	
	//setters e getters
	public String get_protocol() {
		return protocol;
	}
	public void set_protocol(String protocol) {
		this.protocol = protocol;
	}
	public int getSource_port() {
		return source_port;
	}
	public void setSource_port(int source_port) {
		this.source_port = source_port;
	}
	public int getDestination_port() {
		return destination_port;
	}
	public void setDestination_port(int destination_port) {
		this.destination_port = destination_port;
	}
	public int getLength() {
		return length;
	}
	public void setLength(int length) {
		this.length = length;
	}
	
	
}
