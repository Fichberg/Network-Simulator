import java.util.LinkedList;



public class Host extends Node
{
	private String name;               //nome (único) do host
	private String computer_ip;        //endereço IP do computador
    private String router_ip;          //endereço ip do roteador padrao
    private String dns_server_ip;      //endereço ip do servidor dns
    private Agent agent;               //agente de aplicação sobre o host
    private DuplexLink link;           //enlace ao qual está ligado o host
    private LinkedList<Packet> buffer; //buffer (infinito) do host

	//Construtor
	public Host(String name)
	{
		this.name = name;
		this.buffer = new LinkedList<Packet>();
	}

	
	//===============================================
	//MAIN PROGRAM
	@Override
	public void run() {
		
			
	}
	
	
	//===============================================
	//SETTER AND GETTERS
	
	//setters
	public void set_computer_ip(String computer_ip)
	{
		if(this.computer_ip == null)
			this.computer_ip = computer_ip;
	}

	public void set_router_ip(String router_ip)
	{
		if(this.router_ip == null)
			this.router_ip = router_ip;
	}

	public void set_dns_server_ip(String dns_server_ip)
	{
		if(this.dns_server_ip == null)
			this.dns_server_ip = dns_server_ip;
	}
	
	public void set_agent(Agent agent)
	{
		this.agent = agent;
	}
	
	public void set_link(DuplexLink link, String port)
	{
		this.link = link;
	}

	//getters
	public String get_name()
	{
		return this.name;
	}
	
	
	//==============================================
	//COMMUNICATION
	
	//envia pacote pelo enlace do host
	public void send_packet(Packet packet)
	{
		System.out.println("Pacote: " + packet.getId() + " saindo de: " + this.name);
		this.link.forward_packet(this, packet);
	}	
	
	//recebe pacote do enlace do host
	public void receive_packet(DuplexLink link, Packet packet)
	{
		System.out.println("Pacote: " + packet.getId() + " chegando em: " + this.name);
		this.buffer.add(packet);
	}
	
	
	//------------------ NAO IMPLEMENTADO AINDA------------------------
	//==============================================
	//TCP
	
	//inicia uma conexão TCP (faz o 3-way-handshake)
	public void open_connection()
	{
		
	}
	
	//==============================================
	//UDP
	
	//requisita o endereço IP de um host
	public String DNS_lookup(String hostname)
	{
		return null;
	}


	
}
