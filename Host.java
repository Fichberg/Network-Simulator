import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


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
    private boolean in_connection;     //indica se uma conexão TCP foi aberta
    private String assembled;          //guarda dados que estão sendo montados
    private int data_offset;           //a partir de que ponto remontar arquivo

	//Construtor
	public Host(String name)
	{
		this.name = name;
		this.buffer = new LinkedList<Packet>();
		this.sliding_window = 1;
		this.in_connection = false;
		this.assembled = "";
		this.data_offset = 1;
	}

	
	//===============================================
	//MAIN PROGRAM
	@Override
	public void run() {
		
		while(true) 
		{
			synchronized (this) {
				try 
				{
					wait();
				} 
				catch (InterruptedException e) 
				{
					e.printStackTrace();
				}
			}
		}
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
	private void send_packet(Packet packet)
	{
		this.link.forward_packet(this, packet);
	}	
	
	//recebe pacote do enlace do host
	public void receive_packet(DuplexLink link, Packet packet)
	{
		this.buffer.add(packet);
		reply_if_isSYN(packet); //responde handshake

		synchronized (this) 
		{
			notify(); //avisa que um pacote chegou
			
			//remonta pacote a partir de data_offset
			assembly_packet(packet);
			
			if (packet.getApplication() != null)
			{
				reply_if_isACK(packet); //acusa o recebimento
				this.agent.notify_agent(packet); //envia pacote pra aplicação
			}
		}
	}
	
	
	//==============================================
	//TCP
	
	//inicia uma conexão TCP (faz o 3-way-handshake) a partir de uma requisição
	//da camada de aplicação
	private boolean open_connection(Packet app_pack) throws InterruptedException
	{
		//conexão já foi aberta
		if (this.in_connection)
			return true;
		
		//destino inalcançável
		Packet packet = build_raw_TCP_packet(app_pack);
		if (packet == null)
			return false;
		
		TCP transport = (TCP) packet.getTransport();
		transport.setSequence_number(0);
		transport.setACK_number(0);
		transport.setACK(false);
		transport.setSYN(true);
		packet.setTransport(transport);

		//SYN
		int timeout = 10;
		do
		{
			send_packet(packet);
			sleep(100);
			timeout--;
			//sem resposta remota
			if (timeout == 0)
				return false;
		}
		//SYN-ACK
		while (!got_ACK(0));
		
		packet.set_new_Id();
		transport.setSequence_number(1);
		transport.setACK_number(1);
		transport.setACK(true);
		transport.setSYN(false);
		packet.setTransport(transport);
		
		//ACK
		send_packet(packet);
		this.buffer.clear();
		this.in_connection = true;
		return true;
	}
	
	//constrói pacote TCP sem a camada de aplicação
	private Packet build_raw_TCP_packet(Packet app_pack) throws InterruptedException
	{
		ApplicationLayer app = app_pack.getApplication();
		String destination_host = app.get_dest_name();
		
		//resolve o nome do destino
		if (!destination_host.matches("\\d+\\.\\d+\\.\\d+\\.\\d+"))
		{
			destination_host = DNS_lookup(destination_host);
			app.set_dest_name(destination_host);
		}

		if (destination_host == null)
		{
			System.err.println("Destination unreachable");
			return null;
		}
		
		TCP transport_layer = new TCP(app.get_source_port(), app.get_dest_port());
		Packet packet = new Packet("TCP");
		packet.setTransport(transport_layer);
		packet.setIP_source(this.computer_ip);
		packet.setIP_destination(destination_host);
		
		return packet;
	}
	
	
	//constrói pacote TCP a partir da camada de aplicação
	private Packet build_TCP_packet(Packet app_pack) throws InterruptedException 
	{
		Packet packet = build_raw_TCP_packet(app_pack);	
		ApplicationLayer app = app_pack.getApplication();
		
		TCP transport_layer = (TCP) packet.getTransport();
		transport_layer.setLength(app.get_length());
		
		packet.setApplication(app_pack.getApplication());
		packet.setTransport(transport_layer);
		packet.set_data(app_pack.get_data());
		packet.setLength(transport_layer.getLength());
		
		return packet;
	}
	
	//fragmenta pacotes de acordo com o MSS = 1460
	private int chop_data(Packet packet, int offset)
	{
		String data = packet.get_data();
		char[] data_in_chars = data.toCharArray();
		int tam_copiado = 0;
		if (data.length() - (offset + 1460) >= 0)
			tam_copiado = 1460;
		else
			tam_copiado = data.length() - offset + 1;
		
		String chopped_data = String.copyValueOf(data_in_chars, offset-1, tam_copiado);
		packet.set_data(chopped_data);
		return offset + 1460;
	}
	
	//envia um pacote TCP de acordo com a política de controle de congestionamento
	public void send_TCP_packet(Packet app_pack) throws InterruptedException
	{
		//faz 3-way-handshake
		if (!open_connection(app_pack))
			return;

		//constrói pacote TCP
		Packet packet = build_TCP_packet(app_pack);
		if (packet == null)
			return;
		
		int ACK = (int)Math.round(Math.random()*1000) + 2; //numero entre 2 e 1001
		
		//mandando pacotes fragmentados
		if (packet.getLength() > 1460)
		{
			int total = packet.get_data().length() / 1460 + 1;
			int SEQ = 1;	
			Packet[] packets = new Packet[total];
			
			for (int i = 0; i < total; i++)
			{
				Packet p = new Packet("TCP");
				packet.clone_to_packet(p);
				p.setApplication(null);
				TCP transport = new TCP();
				p.getTransport().clone_to(transport);
				transport.setACK_number(ACK);
				transport.setSequence_number(SEQ);
				if (i == total-1) 
				{
					transport.setFIN(true);
					p.setApplication(app_pack.getApplication());
				}
				p.setTransport(transport);
				p.set_data(packet.get_data());
				SEQ = chop_data(p, SEQ);
				packets[i] = p;
			}
			send_with_congestion_control(packets);
		}
		
		//mandando apenas um pacote
		else 
		{
			TCP transport = (TCP) packet.getTransport();
			
			transport.setACK_number(ACK);
			transport.setSequence_number(1);
			transport.setFIN(true);
			packet.setTransport(transport);
			
			do
			{
				send_packet(packet);
				synchronized (this) 
				{
					wait(100); //timeout
				}
			}
			while(!got_ACK(ACK));
		}
			
	}
	
	//verifica se algum pacote da fila tem o ACK esperado = seqNumber
	private boolean got_ACK(int ACK)
	{
		@SuppressWarnings("unchecked")
		LinkedList<Packet> buf = (LinkedList<Packet>) this.buffer.clone();
		Iterator<Packet> itr = buf.iterator();
		while (itr.hasNext())
		{
			Packet packet = itr.next();
			TCP transport = (TCP) packet.getTransport();
			if (transport.getSequence_number() == ACK)
			{
				this.buffer.remove(packet);
				return true;
			}
		}
		return false;
	}
	
	//política de controle de congestionamento
	private void send_with_congestion_control(Packet[] packets) throws InterruptedException
	{
		boolean recebeu = false;
		ArrayList<Integer> acks = new ArrayList<Integer>();
		this.sliding_window = 1;
		
		for (int curr_packet = 0; curr_packet < packets.length; curr_packet++)
		{
			//envia uma janela de pacotes
			for (int i = 0; i < this.sliding_window; i++)
			{	
				TCP transport = (TCP) packets[curr_packet].getTransport();
				acks.add(transport.getACK_number());
				send_packet(packets[curr_packet++]);
				if (curr_packet == packets.length)
					break;
			}
			curr_packet--;
			
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
				}
			}
			if (curr_packet < -1)
				curr_packet = -1;

			//dobra o envio de pacotes em caso de sucesso
			if (recebeu) 
				this.sliding_window *= 2;
			else
				this.sliding_window = 1;
			acks.clear();
		}
	}
	
	//se um pacote TCP tiver bit ACK ligado, responde host acusando recebimento
	private void reply_if_isACK(Packet packet)
	{
		
		TransportLayer tl = packet.getTransport();
		if (tl instanceof TCP)
		{
			TCP transport = (TCP) tl;
			if (transport.isACK())
			{		
				Packet ack_packet = new Packet("TCP");
				ack_packet.setIP_source(this.computer_ip);
				ack_packet.setIP_destination(packet.getIP_source());
				
				TCP ack_transport = new TCP(transport.getDestination_port(), 
						transport.getSource_port());
				
				//determina o offset numa transferência de arquivos
				int next_offset = transport.getACK_number();
				if (next_offset > 1)
				{
					next_offset = transport.getSequence_number() + 1460;
					this.data_offset = next_offset;
				}
				ack_transport.setSequence_number(transport.getACK_number());
				ack_transport.setACK_number(next_offset);
				if (transport.isFIN())
					ack_transport.setFIN(true);
								
				ack_packet.setTransport(ack_transport);
				send_packet(ack_packet);
			}
		}
		
	}
	
	//se um pacote estiver com o bit SYN ligado, responde o host para fazer handshake
	private void reply_if_isSYN(Packet packet)
	{
		
		TransportLayer tl = packet.getTransport();
		if (tl instanceof TCP)
		{
			TCP transport = (TCP) tl;
			if (transport.isSYN() && !transport.isACK())
			{
				this.buffer.remove(packet);
				
				Packet synack_packet = new Packet("TCP");
				synack_packet.setIP_source(this.computer_ip);
				synack_packet.setIP_destination(packet.getIP_source());
				
				TCP synack_transport = new TCP(transport.getDestination_port(), 
						transport.getSource_port());
				synack_transport.setSYN(true);
				synack_transport.setACK_number(1);
				synack_transport.setSequence_number(0);
				
				synack_packet.setTransport(synack_transport);
				this.in_connection = true;
				send_packet(synack_packet);
			}
		}
	}
	
	//monta um pacote que veio fragmentado. Retorna true se terminou a montagem
	private void assembly_packet(Packet packet)
	{
		if (packet.getTransport() instanceof TCP)
		{
			TCP transport = (TCP) packet.getTransport();
			
			//detectando pacote possivelmente fragmentado
			if (transport.isACK() && (transport.getACK_number() > 1))
			{
				//verifica se o pacote recebido veio na ordem correta
				int offset = transport.getSequence_number();
				if (offset != this.data_offset)
					return;
				
				reply_if_isACK(packet);
				this.assembled += packet.get_data();
				
				if (transport.isFIN() && packet.getApplication() != null)
				{
					packet.set_data(this.assembled);
					packet.setLength(this.assembled.length());
					this.assembled = "";
					this.data_offset = 1;
					if (!packet.getApplication().get_text().startsWith("226")) //PUT nope
						this.agent.notify_agent(packet);
				}
					
			}
		}
	}
	
	//==============================================
	//UDP
	
	//requisita o endereço IP de um host
	public String DNS_lookup(String hostname) throws InterruptedException
	{
		String protocol = "DNS";
		String text = hostname + ": type A, class IN";
		String dest = this.dns_server_ip;
		ApplicationLayer app = new ApplicationLayer(protocol, text, "", dest, 53, 34628);
		Packet packet = new Packet("UDP");
		packet.setApplication(app);
		packet = build_UDP_packet(packet);
		
		send_packet(packet);
		synchronized (this) 
		{
			int timeout = 10;
			while (timeout != 0)
			{
				String response = DNS_resolve();
				if (response != null)
					return response;
				wait(300);
				timeout--;
			}
		}
		
		return null;
	}
	
	//extrai o endereço devolvido por um servidor DNS
	private String DNS_resolve()
	{
		@SuppressWarnings("unchecked")
		LinkedList<Packet> buf = (LinkedList<Packet>) this.buffer.clone();
		Iterator<Packet> itr = buf.iterator();
		Pattern p = Pattern.compile("addr (\\d+\\.\\d+\\.\\d+\\.\\d+)");

		//procura uma requisição DNS na lista de pacotes recebidos
		while (itr.hasNext())
		{
			Packet packet = itr.next();
			if (packet.getApplication() != null)
			{
				Matcher m = p.matcher(packet.getApplication().get_text());
				this.buffer.remove(packet);
				if (m.find())
					return m.group(1);
			}
		}

		//endereço não encontrado
		return null;
	}
	
	//recebe um pacote com a camada de aplicação e insere uma camada UDP
	private Packet build_UDP_packet(Packet app_pack)
	{
		ApplicationLayer app = app_pack.getApplication();
		String destination   = app.get_dest_name();
		UDP transport_layer  = new UDP(app.get_source_port(), app.get_dest_port());
		String length  = String.valueOf(transport_layer.getSource_port());
		       length += String.valueOf(transport_layer.getDestination_port());
		transport_layer.setLength(app.get_length() + length.length());
		
		app_pack.setLength(transport_layer.getLength());
		app_pack.setIP_source(this.computer_ip);
		app_pack.setIP_destination(destination);
		app_pack.setTransport(transport_layer);

		return app_pack;
	}
	
	//envia um pacote UDP (visível para a camada de aplicação)
	public void send_UDP_packet(Packet app_pack)
	{
		Packet packet = build_UDP_packet(app_pack);
		send_packet(packet);
	}
	
	


	
}
