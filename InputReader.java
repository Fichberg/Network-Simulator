import java.io.*;
import java.net.*;
import java.util.*;
import java.util.regex.*;

public class InputReader
{
	private HashMap<String, Host> hosts;         //Dicionário de objetos do tipo Host
	private HashMap<String, Router> routers;     //Dicionário de objetos do tipo Router
	private HashMap<String, Node> nodes;         //Dicionário de nós da rede
	private HashMap<String, Agent> agents;       //Dicionário de agentes da rede
	private ArrayList<DuplexLink> duplex_links;  //Lista de objetos do tipo DuplexLink


	//Construtor
	public InputReader(String file_name)
	{
		this.hosts   = new HashMap<String, Host>();
		this.routers = new HashMap<String, Router>();
		this.nodes   = new HashMap<String, Node>();
		this.agents  = new HashMap<String, Agent>();
		this.duplex_links = new ArrayList<DuplexLink>();
		read_input(file_name);
	}

	//Leitor de entrada. Responsável por obter os dados da simulação
	void read_input(String file_name)
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
    		//TODO: acertar a linha dentro do print
		    //System.err.println("InputReader.java:27: " + e);
		}
		catch (IOException e)
		{
			//TODO: acertar a linha dentro do print
		    //System.err.println("InputReader.java:31: " + e);
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
				//TODO: acertar a linha dentro do print
		    	//System.err.println("InputReader.java:44: " + e);
		    }
		}
	}
	
	//Faz o parsing individual de cada linha e faz o dispatch para a função apropriada
	/*===============================================================================*/
	void parse_line(String line) 
	{
		//define todos os padrões possíveis de serem encontrados numa linha
		Pattern[] patterns =
		{
			//0: empty or commented line
			Pattern.compile("^\\s*\\n|^#\\s*\\w+"),
				
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
			Pattern.compile("\\$\\w+ attach-agent \\$(\\w+) \\$(\\w+)\\s*\\n"), 
			
			//10: sniffer-link association
			Pattern.compile("\\$\\w+ attach-agent \\$(\\w+) \\$(\\w+\\.?\\d+) "
					+ "\\$(\\w+\\.?\\d+) \"(.+)\""), 
			
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
					case 0: break;
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
	
	//cria uma instância nova de um host e coloca-o no dicionário
	void set_host(String host) 
	{
		Host h = new Host(host);
		this.nodes.put(host, h);
		this.hosts.put(host, h);
	}
	
	//cria uma instância nova de um router e coloca-o no dicionário 
	void set_router(String router, String interfaces)
	{
		int nr_ports = Integer.parseInt(interfaces);
		Router r = new Router(router, nr_ports);
		this.nodes.put(router, r);
		this.routers.put(router, r);
	}
	
	//cria uma instância nova de um duplex_link e coloca na lista correspondente
	void set_duplex_link(String point_A, String point_B, String capacity, String latency)
	{
		//desprezando as portas POR ENQUANTO
		point_A = point_A.replaceAll("\\.\\d+", "");
		point_B = point_B.replaceAll("\\.\\d+", "");

		Node A = this.nodes.get(point_A);
		Node B = this.nodes.get(point_B);
		DuplexLink link = new DuplexLink(A, B, Float.parseFloat(capacity), Float.parseFloat(latency));
		this.duplex_links.add(link);
	}
	
	//configura um host com IP, default gateway e DNS
	void configure_host(String host, String IP, String router, String DNS)
	{
		Host h = this.hosts.get(host);
		h.set_computer_ip(IP);
		h.set_router_ip(router);
		h.set_dns_server_ip(DNS);
	}
	
	//configura um router associando um IP a uma interface do enlace
	void configure_router(String router, String line)
	{
		Pattern p = Pattern.compile("(\\d+) (\\d+\\.\\d+\\.\\d+\\.\\d+)");
		Matcher m = p.matcher(line);
		Router r  = this.routers.get(router);
		while (m.find()) 
			r.set_ip_port(m.group(2), Integer.parseInt(m.group(1)));
	}
	
	//define as rotas do roteador
	void configure_router_route(String router, String line)
	{
		Pattern p = Pattern.compile("(\\d+\\.\\d+\\.\\d+\\.\\d+) (\\d+\\s|\\d+\\.\\d+\\.\\d+\\.\\d+)");
		Matcher m = p.matcher(line);
		Router r = this.routers.get(router);
		while (m.find()) 
			r.set_route(m.group(1), m.group(2));
	}
	
	//define as especificações de desempenho do roteador
	void configure_router_specs(String router, String time, String line)
	{
		Pattern p = Pattern.compile("(\\d+) (\\d+)");
		Matcher m = p.matcher(line);
		Router r  = this.routers.get(router);
		while (m.find())
			r.set_buffer_size(Integer.parseInt(m.group(1)), m.group(2));
	}
	
	//cria instância de um agente da rede (aplicação ou sniffer)
	void set_agent(String agent, String type)
	{
		AgentEnum a = AgentEnum.valueOf(type);
		switch(a)
		{
			case DNSServer:
				DNSServer dnss = new DNSServer(agent);
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
				Sniffer sniffer = new Sniffer(agent);
				this.agents.put(agent, sniffer);
				break;
			default: break;
		}
	}
	
	//associa um agente de aplicação a um host
	void attach_app_agent(String agent, String host)
	{
		Agent a = this.agents.get(agent);
		Host h  = this.hosts.get(host);
		h.set_agent(a);
	}
	
	//associa um agente sniffer a um duplex_link
	void attach_sniffer_agent(String agent, String point_A, String point_B, String file)
	{
		Iterator<DuplexLink> itr = this.duplex_links.iterator();
		while (itr.hasNext())
		{
			DuplexLink current_dl = itr.next();
			if (current_dl.has_edges(point_A, point_B))
			{
				Sniffer sniffer = (Sniffer) this.agents.get(agent);
				current_dl.set_sniffer(sniffer);
			}
		}
		
	}
	
	//define o programa principal
	//TODO: terminar essa meleca no NetSim
	void set_simulation(String time, String command)
	{
		System.out.println("set_simulation: " + time + " " + command);
	}
	
}
