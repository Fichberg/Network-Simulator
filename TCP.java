
//classe que armazena os dados do TCP na camada de transporte
public class TCP extends TransportLayer {
	
	private String protocol;
	private int source_port;
	private int destination_port;
	private int length;
	private int sequence_number;
	private int ACK_number;
	private boolean ACK;
	private boolean SYN;
	private boolean FIN;
	
	//Construtores
	public TCP(int source, int destination) 
	{
		this.source_port = source;
		this.destination_port = destination;
		this.ACK = true;
		this.SYN = false;
		this.FIN = false;
		this.protocol = "TCP";
	}
	public TCP()
	{
		this.ACK = true;
		this.SYN = false;
		this.FIN = false;
		this.protocol = "TCP";
	}
	
	//getters e setters
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
	public int getSequence_number() {
		return sequence_number;
	}
	public void setSequence_number(int sequence_number) {
		this.sequence_number = sequence_number;
	}
	public int getACK_number() {
		return ACK_number;
	}
	public void setACK_number(int aCK_number) {
		ACK_number = aCK_number;
	}
	public boolean isACK() {
		return ACK;
	}
	public void setACK(boolean aCK) {
		ACK = aCK;
	}
	public boolean isSYN() {
		return SYN;
	}
	public void setSYN(boolean sYN) {
		SYN = sYN;
	}
	public boolean isFIN() {
		return FIN;
	}
	public void setFIN(boolean fIN) {
		FIN = fIN;
	}
	
	//clona um objeto TCP com as características deste
	public void clone_to(TransportLayer tl)
	{
		TCP tcp = (TCP) tl;
		tcp.set_protocol(this.protocol);
		tcp.setSource_port(this.source_port);
		tcp.setDestination_port(this.destination_port);
		tcp.setLength(this.length);
		tcp.setSequence_number(this.sequence_number);
		tcp.setACK_number(this.ACK_number);
		tcp.setACK(this.ACK);
		tcp.setSYN(this.SYN);
		tcp.setFIN(this.FIN);
	}
	

}
