import java.io.*;
import java.util.*;
import java.util.regex.*;

public class InputReader
{
	private HashMap<String, Host> hosts;         //Dicionário de objetos <Nome do host, Host>
	private HashMap<String, Router> routers;     //Dicionário de objetos <Nome do router, Router>
	private HashMap<String, Node> nodes;         //Dicionário de nós <Nome do nó, Node>
	private HashMap<String, Agent> agents;       //Dicionário de agentes <Nome do agente, Agent>
	private HashMap<String, Node> apps;          //Dicionário do tipo <Nome do agente, Node>  
	private HashMap<Float, String> commands;     //Dicionário com o programa do simulador
	private HashMap<String, String> dns_table;   //Tabela de hosts para DNS
	private ArrayList<DuplexLink> duplex_links;  //Lista de objetos do tipo DuplexLink


	private Clock clock;                         //Detém o tempo inicial da execucao. Sera passada para os sniffers

	//Construtor
	public InputReader(String file_name, Clock clock)
	{
		this.clock    = clock;
		this.hosts    = new HashMap<String, Host>();
		this.routers  = new HashMap<String, Router>();
		this.nodes    = new HashMap<String, Node>();
		this.agents   = new HashMap<String, Agent>();
		this.apps     = new HashMap<String, Node>();
		this.commands = new HashMap<Float, String>();
		this.dns_table = new HashMap<String, String>();
		this.duplex_links = new ArrayList<DuplexLink>();
		read_input(file_name);
	}

	//======================================
	//READING
	
	//Leitor de entrada. Responsável por obter os dados da simulação
	public void read_input(String file_name)
	{
		File file = new File(System.getProperty("user.dir") + "/inputs/" + file_name);
		BufferedReader file_reader = null;

		try
		{
    		file_reader = new BufferedReader(new FileReader(file));
    		String line;
    		while( (line = file_reader.readLine()) !=  null)
    			parse_line(line);
    	}
    	catch (FileNotFoundException e)
    	{
    		e.printStackTrace();
		}
		catch (IOException e)
		{
			e.printStackTrace();
		}
		finally
		{
		    try
		    {
		        if (file_reader != null)
		            file_reader.close();
		    }
		    catch (IOException e)
		    {
				e.printStackTrace();
		    }
		}
	}

	//========================================
	//PARSING
	
	//Faz o parsing individual de cada linha e faz o dispatch para a função apropriada
	private void parse_line(String line) 
	{
		//define todos os padrões possíveis de serem encontrados numa linha
		Pattern[] patterns =
		{
			//0: empty or commented line
			Pattern.compile("^\\s*\\n|^#\\s*[\\w\\$]+"),
				
			//1: host setting
			Pattern.compile("set (\\w+) \\[\\$\\w+ host\\]"),
			
			//2: router setting
			Pattern.compile("set (\\w+) \\[\\$\\w+ router (\\d+)\\]"),
			
			//3: duplex-link setting
			Pattern.compile("\\$\\w+ duplex-link \\$(\\w+\\.*\\d*) \\$(\\w+\\.*\\d*) "
					+ "(\\d+)[Mm]bps (\\d+)ms"),
					
			//4: host configuration
			Pattern.compile("\\$\\w+ \\$(\\w+) (\\d+\\.\\d+\\.\\d+\\.\\d+) "
					+ "(\\d+\\.\\d+\\.\\d+\\.\\d+) (\\d+\\.\\d+\\.\\d+\\.\\d+)"),
					
			//5: router configuration
			Pattern.compile("\\$\\w+ \\$(\\w+) \\d+ \\d+\\.\\d+\\.\\d+\\.\\d+"),
			
			//6: route definition
			Pattern.compile("\\$\\w+ \\$(\\w+) route"),
			
			//7: router characteristics
			Pattern.compile("\\$\\w+ \\$(\\w+) performance (\\d+)us"), 
			
			//8: application/sniffer agent settings
			Pattern.compile("set (\\w+) \\[new Agent/(\\w+)\\]"), 
			
			//9: agent-host association
			Pattern.compile("\\$\\w+ attach-agent \\$(\\w+) \\$(\\w+)$"), 
			
			//10: sniffer-link association
			Pattern.compile("\\$\\w+ attach-agent \\$(\\w+) \\$(\\w+\\.?\\d+) " + "\\$(\\w+\\.?\\d+) \"(.*)\""),
			
			//11: main program
			Pattern.compile("\\$\\w+ at (\\d+\\.?\\d+) \"(.+)\"") 
		};
		
		//varre todos os padrões em busca de um match na linha
		for (int i = 0; i < patterns.length; i++)
		{
			Matcher m = patterns[i].matcher(line);
			if (m.find())
			{
				switch(i)
				{
					case 0: return;
					case 1: set_host(m.group(1)); break; 
					case 2: set_router(m.group(1), m.group(2)); break; 
					case 3: set_duplex_link(m.group(1), m.group(2), m.group(3), m.group(4)); break; 
					case 4: configure_host(m.group(1), m.group(2), m.group(3), m.group(4)); break; 
					case 5: configure_router(m.group(1), line); break; 
					case 6: configure_router_route(m.group(1), line); break; 
					case 7: configure_router_specs(m.group(1), m.group(2), line); break; 
					case 8: set_agent(m.group(1), m.group(2)); break; 
					case 9: attach_app_agent(m.group(1), m.group(2)); break; 
					case 10: attach_sniffer_agent(m.group(1), m.group(2), m.group(3), m.group(4)); break;
					case 11: set_simulation(m.group(1), m.group(2)); break;
					default: break; 
				
				}
			}
		}
	}
	
	//1: cria uma instância nova de um host e coloca-o no dicionário
	private void set_host(String host) 
	{
		Host h = new Host(host);
		this.nodes.put(host, h);
		this.hosts.put(host, h);
	}
	
	//2: cria uma instância nova de um router e coloca-o no dicionário 
	private void set_router(String router, String interfaces)
	{
		int nr_ports = Integer.parseInt(interfaces);
		Router r = new Router(router, nr_ports);
		this.nodes.put(router, r);
		this.routers.put(router, r);
	}
	
	//3: cria uma instância nova de um duplex_link e coloca na lista correspondente
	private void set_duplex_link(String point_A, String point_B, String capacity, String latency)
	{
		//desprezando as portas
		String pointA = point_A.replaceAll("\\.\\d+", "");
		String pointB = point_B.replaceAll("\\.\\d+", "");

		//buscando os Nodes que farão parte do link
		Node A = this.nodes.get(pointA);
		Node B = this.nodes.get(pointB);
		
		//inicializando o link com as pontas e características próprias
		DuplexLink link = new DuplexLink(A, B, Float.parseFloat(capacity), Float.parseFloat(latency));
		link.set_link(point_A, point_B);
		this.duplex_links.add(link);
	}
	
	//4: configura um host com IP, default gateway e DNS
	private void configure_host(String host, String IP, String router, String DNS)
	{
		Host h = this.hosts.get(host);
		h.set_computer_ip(IP);
		h.set_router_ip(router);
		h.set_dns_server_ip(DNS);
		this.dns_table.put(host, IP);
	}
	
	//5: configura um router associando um IP a uma interface do enlace
	private void configure_router(String router, String line)
	{
		Pattern p = Pattern.compile("(\\d+) (\\d+\\.\\d+\\.\\d+\\.\\d+)");
		Matcher m = p.matcher(line);
		Router r  = this.routers.get(router);
		while (m.find()) 
			r.set_ip_port(m.group(2), Integer.parseInt(m.group(1)));
	}
	
	//6: define as rotas do roteador
	private void configure_router_route(String router, String line)
	{
		Pattern p = Pattern.compile("(\\d+\\.\\d+\\.\\d+\\.\\d+) (\\d+\\s|\\d+\\.\\d+\\.\\d+\\.\\d+)");
		Matcher m = p.matcher(line);
		Router r = this.routers.get(router);
		while (m.find()) 
			r.set_route(m.group(1).trim(), m.group(2).trim());
	}
	
	//7: define as especificações de desempenho do roteador
	private void configure_router_specs(String router, String time, String line)
	{
		Pattern p = Pattern.compile("(\\d+) (\\d+)");
		Matcher m = p.matcher(line);
		Router r  = this.routers.get(router);
		while (m.find())
			r.set_buffer_size(Integer.parseInt(m.group(1)), Integer.parseInt(m.group(2)));
	}
	
	//8: cria instância de um agente da rede (aplicação ou sniffer)
	private void set_agent(String agent, String type)
	{
		AgentEnum a = AgentEnum.valueOf(type);
		switch(a)
		{
			case DNSServer:
				DNSServer dnss = new DNSServer(agent, this.dns_table);
				this.agents.put(agent, dnss);
				break;
			case FTPClient:
				FTPClient ftpc = new FTPClient(agent);
				this.agents.put(agent, ftpc);
				break;
			case FTPServer:
				FTPServer ftps = new FTPServer(agent);
				this.agents.put(agent, ftps);
				break;
			case HTTPClient:
				HTTPClient httpc = new HTTPClient(agent);
				this.agents.put(agent, httpc);
				break;
			case HTTPServer:
				HTTPServer https = new HTTPServer(agent);
				this.agents.put(agent, https);
				break;
			case Sniffer:
				Sniffer sniffer = new Sniffer(agent, this.clock);
				this.agents.put(agent, sniffer);
				break;
			default: break;
		}
	}
	
	//9: associa um agente de aplicação a um host
	private void attach_app_agent(String agent, String host)
	{
		Agent a = this.agents.get(agent);
		Host h  = this.hosts.get(host);
		h.set_agent(a);
		a.set_residence(h);
		this.apps.put(agent, h);
	}
	
	//10: associa um agente sniffer a um duplex_link
	private void attach_sniffer_agent(String agent, String point_A, String point_B, String filename)
	{
		Iterator<DuplexLink> itr = this.duplex_links.iterator();
		while (itr.hasNext())
		{
			DuplexLink current_link = itr.next();
			if (current_link.has_edges(point_A, point_B))
			{
				Sniffer sniffer = (Sniffer) this.agents.get(agent);
				sniffer.set_filename(filename);
				current_link.set_sniffer(sniffer);
				break;
			}
		}	
	}
	
	//11: define o programa principal
	private void set_simulation(String time, String command)
	{
		this.commands.put(Float.parseFloat(time), command);
	}
	
	//=================================
	//SETTERS AND GETTERS
	
	//devolve um dicionário tempo de chamada (segs) -> comando  
	public HashMap<Float, String> get_simulator_program() 
	{
		return this.commands;
	}
	
	//devolve um Node
	public Node get_node(String node)
	{
		return this.nodes.get(node);
	}
	
	//devolve uma lista com todos os Nodes
	public Node[] get_all_nodes()
	{
		Node[] node_list = new Node[this.nodes.size()];
		node_list = this.nodes.values().toArray(node_list);
		return node_list;
	}
	
	//devolve um agente de aplicação
	public Agent get_agent(String agent)
	{
		return this.agents.get(agent);
	}
	
}
