import java.util.HashMap;

public class DNSServer extends Agent {
	
	private String name;                   //nome do servidor DNS
	private Node node;                     //Host que hospeda este servidor
	private HashMap<String, String> table; //tabela de hosts do servidor DNS
	
	//Construtor
	public DNSServer(String name, HashMap<String, String> table)
	{
		this.name = name;
		this.table = table;
	}
	
	//getters
	public String get_name() 
	{
		return this.name;
	}
	
	public String get_type()
	{
		return "DNSServer";
	}

	//============================================
	//SERVER CONFIGURATION
	
	//define onde a aplicação está localizada
	public void set_residence(Object obj)
	{
		this.node = (Node) obj;
	}
	
	//adiciona hosts à tabela do servidor DNS
	public void add_host(String hostname, String host_IP)
	{
		this.table.put(hostname, host_IP);
	}
	
	//=============================================
	//COMMUNICATION
	
	//recebe comando do simulador
	public void receive_command(String command) 
	{
		//não utilizado para DNSServer
	}
	
	//alerta recebimento de pacote
	public void notify_agent(Packet packet)
	{
		ApplicationLayer app = packet.getApplication();
		String dest_IP = packet.getIP_source();
		process_DNS_query(app, dest_IP);
	}
	
	//processa uma requisição DNS, respondendo com um pacote contendo o endereço IP
	public void process_DNS_query(ApplicationLayer app, String dest_IP)
	{
		String[] split  = app.get_text().split(":");
		String hostname = split[0];
		String host_IP  = this.table.get(hostname);
		String appended = " addr " + host_IP;
		
		//adiciona header modificado
		app.set_length(app.get_length() + appended.length());
		app.set_text(app.get_text() + appended);
		app.set_dest_name(dest_IP);
		
		//inverte as portas de origem e destino
		int temp_port = app.get_dest_port();
		app.set_dest_port(app.get_source_port());
		app.set_source_port(temp_port);
		
		Packet packet = new Packet();
		packet.setApplication(app);
		
		Host h = (Host) this.node;
		h.send_UDP_packet(packet);
	}

}
