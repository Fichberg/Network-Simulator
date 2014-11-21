import java.util.UUID;


public class Packet {

	private UUID id;                       //identificador único do pacote
	private ApplicationLayer application;  //dados da camada de aplicação
	private TransportLayer transport;      //dados da camada de transporte
	private int length;                    //tamanho total do pacote
	private String IP_source;              //IP de origem
	private String IP_destination;         //IP de destino
	private int protocol;                  //Protocolo da camada de transporte [TCP ou UDP]
	private int TTL;                       //Time-to-live
	private String data;                   //dados enviados pelo pacote
	
	
	//Construtor
	public Packet() {
		this.id = UUID.randomUUID();
	}
	
	//getters e setters (GERADO AUTOMATICAMENTE, REVISAR!)
	public UUID getId() {
		return id;
	}
	public ApplicationLayer getApplication() {
		return application;
	}
	public void setApplication(ApplicationLayer application) {
		this.application = application;
	}
	public TransportLayer getTransport() {
		return transport;
	}
	public void setTransport(TransportLayer transport) {
		this.transport = transport;
	}
	public int getLength() {
		return length;
	}
	public void setLength(int length) {
		this.length = length;
	}
	public String getIP_source() {
		return IP_source;
	}
	public void setIP_source(String iP_source) {
		IP_source = iP_source;
	}
	public String getIP_destination() {
		return IP_destination;
	}
	public void setIP_destination(String iP_destination) {
		IP_destination = iP_destination;
	}
	public int getProtocol() {
		return protocol;
	}
	public void setProtocol(int protocol) {
		this.protocol = protocol;
	}
	public int getTTL() {
		return TTL;
	}
	public void setTTL(int tTL) {
		TTL = tTL;
	}
	
	
	public String get_data()
	{
		return this.data;
	}
	
	public void set_data(String data)
	{
		this.data = data;
	}

	//copia os dados do pacote atual em outro passado como parâmetro
	public void clone_to_packet(Packet packet)
	{
		packet.setApplication(this.application);
		packet.setIP_destination(this.IP_destination);
		packet.setIP_source(this.IP_source);
		packet.setProtocol(this.protocol);
		packet.setLength(this.length);
		packet.setTransport(this.transport);
		packet.setTTL(this.TTL);
	}
	
}
