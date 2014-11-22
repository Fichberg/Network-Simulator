
import java.util.*;

public class Router extends Node
{
	private String name;                           //identificador único do router. 
	private int interfaces;                        //número de interfaces que este router tem.
	private float performance;                     //tempo para processar 1 pacote em ms;
	private HashMap<String, Integer> ip_port;      //dicionário IP -> interface associada.
	private HashMap<String, String> routes;        //tabela de roteamento do router.
	private HashMap<Integer, RouterBuffer> buffers;//filas dos pacotes associadas a uma interface
	private HashMap<DuplexLink, Integer> links;    //dicionário enlace -> porta associada
	private int current_port;                      //indica qual a porta está sendo processada

	//Construtor
	public Router(String name, int interfaces)
	{
		this.name = name;
		this.interfaces = interfaces;
		this.ip_port = new HashMap<String, Integer>();
		this.routes = new HashMap<String, String>();
		this.buffers = new HashMap<Integer, RouterBuffer>();
		this.links = new HashMap<DuplexLink, Integer>();
		this.current_port = 0;
	}

	//======================================
	//MAIN PROGRAM
	@Override
	public void run() {
	
		//atualiza a tabela de roteamento
		update_routing();
		
		while (true)
		{
			try 
			{   //tempo de latência do roteador para processar pacote
				sleep((long) this.performance);//this.performance);
			} catch (InterruptedException e) 
			{
				e.printStackTrace();
				break;
			}
			Packet packet = process_package();
			if (packet == null)
				continue;
			int port = route_from_packet(packet);
			send_packet(packet, port);
		}
	}
	
	
	//======================================
	//SETTERS AND GETTERS
	
	//setters
	public void set_ip_port(String IP, int port)
	{
		this.ip_port.put(IP, port);
	}
	
	public void set_route(String source, String destiny)
	{
		this.routes.put(source, destiny);
	}
	
	public void set_performance(float performance)
	{
		//salva em milissegs
		this.performance = performance / 1000;
	}
	
	public void set_buffer_size(int port, int size)
	{
		RouterBuffer buffer = new RouterBuffer(port, size);
		this.buffers.put(port, buffer);
	}

	public void set_link(DuplexLink link, String port) 
	{
		if (port != null)
			this.links.put(link, Integer.parseInt(port));
	}

	//getters
	public String get_name()
	{
		return this.name;
	}

	public int get_interfaces()
	{
		return this.interfaces;
	}
	
	public float get_performance()
	{
		return this.performance;
	}
	
	//retorna o DuplexLink associado a uma porta
	public DuplexLink get_link(int port)
	{
		if (this.links.containsValue(port)) 
		{
			Iterator<DuplexLink> itr = this.links.keySet().iterator();
			while (itr.hasNext()) 
			{
				DuplexLink link = itr.next();
				if (this.links.get(link) == port)
					return link;
			}
		}
		return null;
	}
	
	//Checa se a porta existe no atributo de portas e IP
	public boolean have_port(int port)
	{
		return this.ip_port.containsKey(port);
	}

	//Checa se o IP existe no atributo de portas e strings
	public boolean have_ip(String ip)
	{
		return this.ip_port.containsValue(ip);
	}

	//Busca o IP associado à porta
	public String retrieve_associated_ip(int port)
	{
		if (this.ip_port.containsValue(port)) 
		{
			Iterator<String> itr = this.ip_port.keySet().iterator();
			while (itr.hasNext()) 
			{
				String current_ip = itr.next();
				if (this.ip_port.get(current_ip) == port)
					return current_ip;
			}
		}
		return null;
	}

	//Busca a porta associado ao IP
	public int retrieve_associated_port(String ip)
	{
		if (this.ip_port.containsKey(ip))
			return this.ip_port.get(ip);
		return -1;
	}
	
	//Atualiza a tabela de roteamento para eliminar valores
	//que correspondem a IPs. Queremos IP->porta, não IP->IP.
	private void update_routing()
	{
		Iterator<String> keys = this.routes.keySet().iterator();
		while (keys.hasNext())
		{
			String key = keys.next();
			String value = this.routes.get(key);
			if (value.matches("\\d+\\.\\d+\\.\\d+\\.\\d+"))
			{
				String temp = value.replaceAll("(\\d+\\.\\d+\\.\\d+)\\.\\d+", "$1.0");
				String new_value = this.routes.get(temp);
				this.routes.put(key, new_value);
			}
		}
	}
	
	//==========================================
	//COMMUNICATION
	
	//recebe pacote de um enlace
	public void receive_packet(DuplexLink link, Packet packet)
	{
		int enlace = this.links.get(link);
		System.out.println("Router " + this.name + " recebeu " + packet.getId() + " na porta " + enlace);
		RouterBuffer buffer = this.buffers.get(enlace);
		
		//dropando pacote: buffer lotado
		if (buffer.is_full()) {
			System.out.println("Buffer's full on port "+ enlace +" from router "+ this.name);
			return;	
		}
	
		buffer.put_packet(packet);
	}
	
	//roteia o pacote de uma interface para outra	
	public void send_packet(Packet packet, int destination_port) 
	{
		//System.out.println("Pacote: " + packet.getId() + " passando por: " + this.name);
		DuplexLink link = this.get_link(destination_port);
		link.forward_packet(this, packet);
	}
	
	
	//==========================================
	//PACKAGE PROCESSING
	
	//define a interface para onde deve ser enviado o pacote
	private int route_from_packet(Packet packet)
	{
		String destination_IP = packet.getIP_destination();
		destination_IP = destination_IP.replaceAll("(\\d+\\.\\d+\\.\\d+)\\.\\d+", "$1.0");
		String route = this.routes.get(destination_IP);
		return Integer.parseInt(route);
	}
	
	//retira um pacote de um dos buffers num esquema round-robin
	private Packet process_package()
	{
		if (this.current_port == this.interfaces)
			this.current_port = 0;
		RouterBuffer buffer = this.buffers.get(this.current_port);
		this.current_port++;
		return buffer.pull_packet();	
	}



	
	


}
