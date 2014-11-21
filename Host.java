import java.util.ArrayList;
import java.util.Iterator;
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
    private int sliding_window;        //tamanho da janela de envio de pacotes

	//Construtor
	public Host(String name)
	{
		this.name = name;
		this.buffer = new LinkedList<Packet>();
		this.sliding_window = 1;
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
		this.link.forward_packet(this, packet);
	}	
	
	//recebe pacote do enlace do host
	public void receive_packet(DuplexLink link, Packet packet)
	{
		this.buffer.add(packet);
		if (this.buffer.size() == this.sliding_window)
		{
			synchronized (this) 
			{
				System.out.println("recebi o pacote: " + packet.getId());
				notify(); //avisa que um pacote chegou
			}
		}
	}
	
	
	//==============================================
	//TCP
	
	//inicia uma conexão TCP (faz o 3-way-handshake)
	//precisa abrir o arquivo esquemas/3wayhandshake.pcap no wireshark e dar uma estudada
	public void open_connection()
	{
		
	}
	
	//constrói pacote TCP a partir da camada de aplicação
	public Packet build_TCP_packet(Packet app_pack) 
	{
		ApplicationLayer app = app_pack.getApplication();
		String destination_host = app.get_dest_name();
		
		//resolve o nome do destino
		if (!destination_host.matches("\\d+\\.\\d+\\.\\d+\\.\\d+"))
		{
			System.out.println("DNS_LOOKUP MISSING");
			//TODO DNS_lookup (destination_host);
		}
		
		TCP transport_layer = new TCP(app.get_source_port(), app.get_dest_port());
		app_pack.setTransport(transport_layer);
		app_pack.setLength(transport_layer.getLength()); //+ tamanho do PACKET!
		app_pack.setProtocol(6); //número do TCP
		app_pack.setTTL(64);
		app_pack.setIP_source(this.computer_ip);
		app_pack.setIP_destination(destination_host);
		
		return app_pack;
	}
	
	//fragmenta pacotes de acordo com o MSS = 1460
	public int chop_data(Packet packet, int offset)
	{
		String data = packet.get_data();
		char[] data_in_chars = data.toCharArray();
		String chopped_data = String.copyValueOf(data_in_chars, offset-1, 1460);
		packet.set_data(chopped_data);
		return offset + 1460;
	}
	
	//envia um pacote TCP de acordo com a política de controle de congestionamento
	public void send_TCP_packet(Packet packet) throws InterruptedException
	{
		//mandando pacotes fragmentados
		if (packet.getLength() > 1460)
		{
			int total = packet.getLength() / 1460 + 1;
			int SEQ = 1;
			int ACK = 1;
			Packet[] packets = new Packet[total];
			
			for (int i = 0; i < total; i++)
			{
				Packet p = new Packet();
				packet.clone_to_packet(p);
				TCP transport = (TCP) p.getTransport();
				transport.setACK_number(ACK++);
				transport.setSequence_number(SEQ);
				SEQ = chop_data(p, SEQ);
			}
			send_with_congestion_control(packets);
			
		}
		//mandando apenas um pacote
		else 
		{
			while(!got_ACK(1))
			{
				send_packet(packet);
				synchronized (this) 
				{
					wait(100); //timeout
				}
				
			}
		}
			
	}
	
	//verifica se algum pacote da fila tem o ACK esperado
	private boolean got_ACK(int ACK)
	{
		Iterator<Packet> itr = this.buffer.iterator();
		while (itr.hasNext())
		{
			TCP transport = (TCP) itr.next().getTransport();
			if (transport.getACK_number() == ACK)
				return true;
		}
		return false;
	}
	
	//política de controle de congestionamento
	private void send_with_congestion_control(Packet[] packets) throws InterruptedException
	{
		
		boolean recebeu = false;
		ArrayList<Integer> acks = new ArrayList<Integer>();
		
		for (int curr_packet = 0; curr_packet < packets.length; curr_packet++)
		{
			//envia uma janela de pacotes
			for (int i = 0; i < this.sliding_window; i++)
			{	
				TCP transport = (TCP) packets[curr_packet].getTransport();
				acks.add(transport.getACK_number());
				send_packet(packets[curr_packet++]);
			}
			
			//verifica se a janela enviada chegou ao destino
			Iterator<Integer> ack_itr = acks.iterator();
			synchronized (this) 
			{
				wait(100); //timeout
			}
			
			recebeu = true;
			while(ack_itr.hasNext())
			{
				int ACK = ack_itr.next();
				if (!got_ACK(ACK))
				{
					recebeu = false;
					curr_packet -= this.sliding_window;
					acks.clear();
				}
			}
		
			//dobra o envio de pacotes em caso de sucesso
			if (recebeu)
				this.sliding_window *= 2;
			else
				this.sliding_window = 1;
		}
	}
	
	//==============================================
	//UDP
	
	//requisita o endereço IP de um host
	public String DNS_lookup(String hostname)
	{
		return null;
	}


	
}
