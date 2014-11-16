import java.io.*;
import java.net.*;
import java.util.*;
import java.util.regex.*;

public class InputReader
{
	private ArrayList<Host> hosts;               //Lista de objetos do tipo Host
	private ArrayList<Router> routers;           //Lista de objetos do tipo Router
	private ArrayList<DuplexLink> duplex_links;  //Lista de objetos do tipo DuplexLink

	//Construtor
	////////////
	public InputReader(String file_name)
	{
		this.hosts = new ArrayList<Host>();
		this.routers = new ArrayList<Router>();
		this.duplex_links = new ArrayList<DuplexLink>();
		read_input(file_name);
	}

	//Leitor de entrada. Responsável por obter os dados da simulação
	////////////////////////////////////////////////////////////////
	void read_input(String file_name)
	{
		File file = new File(System.getProperty("user.dir") + "/inputs/" + file_name);
		BufferedReader file_reader = null;

		try
		{
    		file_reader = new BufferedReader(new FileReader(file));
    		String line;
    		while( (line = file_reader.readLine()) !=  null)
    		{
    			if(get_host_from_line(line) == true) continue;
    			else if(get_router_from_line(line) == true) continue;
    			else if(get_duplex_link_from_line(line)) continue;


    			//TODO: terminar os tratamentos das linhas de entrada.
    			create_network_components(line);
    			configure_network_components(line);
    		}
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
		        {
		            file_reader.close();
		        }
		    }
		    catch (IOException e)
		    {
				//TODO: acertar a linha dentro do print
		    	//System.err.println("InputReader.java:44: " + e);
		    }
		}
	}

	
	//Faz o parsing individual de cada linha e retorna...
	//////////////////////////////////////////////////////////////////////////
	void parse_line(String line) 
	{
		Pattern[] patterns = 
		{
			Pattern.compile("set (\\w+) \\[\\$\\w+ host\\]"), //host setting
			Pattern.compile("set (\\w+) \\[\\$\\w+ router (\\d+)\\]"), //router setting
			Pattern.compile("\\$\\w+ duplex-link \\$(\\w+)\\.*(\\d+)* \\$(\\w+)\\.*(\\d+)* "
					+ "(\\d+)[Mm]bps (\\d+)ms"), //duplex-link setting
			Pattern.compile("\\$\\w+ \\$\\w+ (\\d+\\.\\d+\\.\\d+\\.\\d+) (\\d+\\.\\d+\\.\\d+\\.\\d+) "
					+ "(\\d+\\.\\d+\\.\\d+\\.\\d+)"), //host configuration
			Pattern.compile(""), //router configuration
			Pattern.compile(""), //route definition
			Pattern.compile(""), //router characteristics
		};
	}
	
	
	//Retorna o identificador do router ou do host ou ainda o numero de interfaces do router.
	//Uma vez com os componentes de rede criados no programa, os configuraremos
	///////////////////////////////////////////////////////////////////////////
	void configure_network_components(String line)
	{
		configure_host(line);
		configure_router_ip_port(line);
		/*
		TODO next step:
		1) function configure_router_route for inputs like "$simulator $r0 route 10.0.0.0 0 10.1.1.0 1 192.168.3.0 2 192.168.2.0 192.168.3.4 192.168.1.0 192.168.3.4"
		2) function configure_router_performance for inputs like "$simulator $r0 performance 100us 0 1000 1 1000 2 1000"
		later:
		see how will be structure for configuring the agents and later on, the sniffers.
		*/
	}

	//Configura um host com as informacoes passadas pela entrada
	////////////////////////////////////////////////////////////
	void configure_router_ip_port(String line)
	{
		Pattern p = Pattern.compile("(\\$simulator \\$r[0-9]+ [0-9]+){1}?.*");
    	Matcher m = p.matcher(line);
    	if(m.matches())
    	{
    		int router_id = get_number_from_line(line, 13);
    		int number_of_spaces = char_counter(' ', line) - 1, i; //desconsidera o espaço entre os primeiros $
    		String ip_and_ports = line.substring(get_nth_char_index(line, ' ', 2));

    		Router r = get_router_from_list(router_id);
    		if(r != null)
    		{
    			Pattern pport = Pattern.compile("[0-9]+$");//Porta
    			Pattern pip = Pattern.compile("[0-9]+\\.[0-9]+\\.[0-9]+\\.[0-9]+$"); //IP
				for(i = 1; i <= number_of_spaces; i++)
				{
					if(i % 2 == 1)
					{
						String port;
						if(i < number_of_spaces)
							port = ip_and_ports.substring(get_nth_char_index(ip_and_ports, ' ', i) + 1, get_nth_char_index(ip_and_ports, ' ', i + 1));
						else
							port = ip_and_ports.substring(get_nth_char_index(ip_and_ports, ' ', i) + 1);
						Matcher mport = pport.matcher(port);
						if(!mport.matches()) //Erro. Par incompleto Porta:IP. Pode ser problema de ordem na linha ou simplesmente estar faltando um elemento
						//Este erro tmb pode ser desencadeado caso exista espaços a mais (nao ira bater com o padrao)
							missing_ip_port_par(line);
					}
					else
					{
						String ip;
						if(i < number_of_spaces)
							ip = ip_and_ports.substring(get_nth_char_index(ip_and_ports, ' ', i) + 1, get_nth_char_index(ip_and_ports, ' ', i + 1));
						else
							ip = ip_and_ports.substring(get_nth_char_index(ip_and_ports, ' ', i) + 1);
						Matcher mip = pip.matcher(ip);
						if(!mip.matches()) //Erro. Par incompleto Porta:IP. Pode ser problema de ordem na linha ou simplesmente estar faltando um elemento
						//Este erro tmb pode ser desencadeado caso exista espaços a mais (nao ira bater com o padrao)
							missing_ip_port_par(line);
					}
				}
				r.ports_and_ips = ip_and_ports.substring(1);
    		}
    		else
    			failed_to_configure_undeclared_component('R', line);    		
    	}
	}

	//Configura um host com as informacoes passadas pela entrada
	void configure_host(String line)
	{
		Pattern p = Pattern.compile("(\\$simulator \\$h[0-9]+ [0-9]+\\.[0-9]+\\.[0-9]+\\.[0-9]+ [0-9]+\\.[0-9]+\\.[0-9]+\\.[0-9]+ [0-9]+\\.[0-9]+\\.[0-9]+\\.[0-9]+){1}?$");
    	Matcher m = p.matcher(line);
    	if(m.matches())
    	{
    		int host_id = get_number_from_line(line, 13);
    		String computer_ip = get_substring_from_line(line, get_nth_char_index(line, ' ', 2) + 1);
    		String router_ip = get_substring_from_line(line, get_nth_char_index(line, ' ', 3) + 1);
    		String dns_server_ip  = get_substring_from_line(line, get_nth_char_index(line, ' ', 4) + 1);
    		
    		Host h = get_host_from_list(host_id);
    		if(h != null)
    		{
    			h.set_computer_ip(computer_ip);
    			h.set_router_ip(router_ip);
    			h.set_dns_server_ip(dns_server_ip);
    		}
    		else
    			failed_to_configure_undeclared_component('H', line);
    	}
	}

	//Conta as incidencias de ch em line
	int char_counter(char ch, String line)
	{
		int counter = 0, i;
		for(i = 0; i < line.length(); i++)
			if(line.charAt(i) == ch) counter++;
		return counter;
	}

	//Retorna o indice da n-esima ocorrencia nth_char do caracter ch na string line. -1 se nao encontrar o caracter com o numero ocorrencias procurado
	int get_nth_char_index(String line, char ch, int nth_char)
	{
		int counter = 0;
		for(int i = 0; i < line.length(); i++)
		{
			if(line.charAt(i) == ch) counter++;
			if(counter == nth_char) return i;
		}
		return -1;
	}

	//Procura um router pelo seu ID na lista de routers e o retorna
	Router get_router_from_list(int router_id)
	{
		for(Router r : routers)
			if(router_id == r.get_id()) return r;
		return null;
	}

	//Procura um host pelo seu ID na lista de hosts e o retorna
	Host get_host_from_list(int host_id)
	{
		for(Host h : hosts)
			if(host_id == h.get_id()) return h;
		return null;
	}

	//Cria os componentes da rede a ser simulada. A cada linha lida no arquivo, ve-se a informacao refere-se a um dos seguintes componentes.
	//Quando encontrado o tipo do compoenente certo, o cria.
	void create_network_components(String line)
	{
		get_host_from_line(line);
    	get_router_from_line(line);
    	get_duplex_link_from_line(line);
	}

	//Retorna o identificador do router ou do host ou ainda o numero de interfaces do router. 
	//Um router identificado na entrada como r23, tem identificador 23. Idem host, só que com h no lugar do r.
	//first_index e o primeiro indice do numero que buscamos na linha
	int get_number_from_line(String line, int first_index)
	{
		int number_starting_index = first_index, number_ending_index = first_index;
		while(Character.isDigit(line.charAt(number_ending_index + 1))) number_ending_index++;
		return Integer.parseInt(line.substring(number_starting_index, number_ending_index + 1));
	}

	////checa se a linha e de duplex-link. Se sim, o adiciona a duplex_links e retorna true. Caso contrario, retorna false
	void get_duplex_link_from_line(String line)
	{
		Pattern p = Pattern.compile("(\\$simulator duplex-link \\$){1}?((h|r)[0-9]+){1}?(\\.[0-9])?( \\$){1}?((h|r)[0-9]){1}?(\\.[0-9])?( [0-9]+Mbps [0-9]+ms){1}?$");
    	Matcher m = p.matcher(line);
    	if(m.matches())
    	{
    		String point_A, point_B;
    		float mbps_capacity, latency;

    		point_A = get_substring_from_line(line, get_nth_char_index(line, ' ', 2) + 2);
    		point_B = get_substring_from_line(line, get_nth_char_index(line, ' ', 3) + 2);
    		mbps_capacity = get_number_from_line(line, get_nth_char_index(line, ' ', 4) + 1);
    		latency = get_number_from_line(line, get_nth_char_index(line, ' ', 5) + 1);

    		//Os hosts/routers precisam estar na lista
    		if(!check_points_availability(point_A, point_B))
    			not_defined_duplex_link_points_error(point_A, point_B, line);

    		//Insere na lista
    		duplex_links.add(new DuplexLink(point_A, point_B, mbps_capacity, latency));
    	}
	}

	//checa se os pontos existem de fato para que este link exista
	boolean check_points_availability(String point_A, String point_B)
	{
		int point_A_id, point_B_id;
		if(point_A.charAt(0) == 'h')
			point_A_id = Integer.parseInt(point_A.substring(1));
		else
			point_A_id = Integer.parseInt(point_A.substring(1, get_nth_char_index(point_A, '.', 1)));
		if(point_B.charAt(0) == 'h')
			point_B_id = Integer.parseInt(point_B.substring(1));
		else
			point_B_id = Integer.parseInt(point_B.substring(1, get_nth_char_index(point_B, '.', 1)));

		boolean have_A = false, have_B = false;

		if(point_A.charAt(0) == 'h' && !hosts.isEmpty())
		{
			for(Host h : hosts)
				if(h.get_id() == point_A_id)
				{
					have_A = true;
					break;
				}
		}
		else if(point_A.charAt(0) == 'r' && !routers.isEmpty())
		{
			for(Router r : routers)
				if(r.get_id() == point_A_id)
				{
					have_A = true;
					break;
				}
		}
		if(point_B.charAt(0) == 'h'  && !hosts.isEmpty())
		{
			for(Host h : hosts)
				if(h.get_id() == point_B_id)
				{
					have_B = true;
					break;
				}
		}
		else if(point_B.charAt(0) == 'r' && !routers.isEmpty())
		{
			for(Router r : routers)
				if(r.get_id() == point_B_id)
				{
					have_B = true;
					break;
				}
		}
		return have_A && have_B;
	}

	//retorna uma substring do arquivo de entrada. A substring vai de um indice determinado ate o primeiro espaco (' ')
	String get_substring_from_line(String line, int first_index)
	{
		int starting_index = first_index, ending_index; //starting_index e o indice do comeco da string que contem o ponto_A (e um h ou um r)
		for(ending_index = starting_index; ending_index < line.length() && !Character.isSpaceChar(line.charAt(ending_index)); ending_index++) continue;
		return line.substring(starting_index, ending_index);
	}

	//checa se a linha e de router. Se sim, o adiciona a routers e retorna true. Caso contrario, retorna false
	void get_router_from_line(String line)
	{
		Pattern p = Pattern.compile("(set r){1}?[0-9]+( \\[\\$simulator router [0-9]+\\]){1}?$");
    	Matcher m = p.matcher(line);
    	if(m.matches())
    	{
    		int router_id, router_interfaces;

    		router_id = get_number_from_line(line, 5);
    		router_interfaces = get_number_from_line(line, 26);
    		//Coloca o primeiro elemento do tipo Router
    		if(routers.isEmpty())
    			routers.add(new Router(router_id, router_interfaces));
    		//Checa se já existe um router com este id. Caso sim, avisa sobre o erro de entrada e encerra o programa (os identificadores devem ser unicos).
    		//Caso não, adiciona o elemento na lista e prossegue normalmente
    		else
    		{
    			if(!check_id_availability('R', router_id))
    				non_unique_id_input_error('R', router_id);
    			else
    				routers.add(new Router(router_id, router_interfaces));
    		}
    	}
	}

	//checa se a linha e de host. Se sim, o adiciona a hosts e retorna true. Caso contrario, retorna false
	void get_host_from_line(String line)
	{
		Pattern p = Pattern.compile("(set h){1}?[0-9]+( \\[\\$simulator host\\]){1}?$");
    	Matcher m = p.matcher(line);
    	if(m.matches())
    	{
    		int host_id;

    		host_id = get_number_from_line(line, 5);
    		//Coloca o primeiro elemento do tipo Host
    		if(hosts.isEmpty())
    			hosts.add(new Host(host_id));
    		//Checa se já existe um host com este id. Caso sim, avisa sobre o erro de entrada e encerra o programa (os identificadores devem ser unicos).
    		//Caso não, adiciona o elemento na lista e prossegue normalmente
    		else
    		{
    			if(!check_id_availability('H', host_id))
    				non_unique_id_input_error('H', host_id);
    			else
    				hosts.add(new Host(host_id));
    		}
    	}
	}

	//o identificador deve ser único para um host ou um router
	boolean check_id_availability(char type, int id)
	{
		if(type == 'H')
		{
			for(Host h : hosts)
				if(h.get_id() == id) return false;
		}
		if(type == 'R')
		{
			for(Router r : routers)
				if(r.get_id() == id) return false;
		}
		return true;
	}


	//notifica o erro de id não único no arquivo de entrada e encerra o programa
	void non_unique_id_input_error(char type, int id)
	{
		//Host
		if(type == 'H')
			System.out.println("Input error: duplicated host id " + id);
		//Router
		if(type == 'R')
			System.out.println("Input error: duplicated router id " + id);
		//encerra
		System.exit(-1);
	}

	//notifica erro de encontrar um duplex-link na entrada sem que tenha um dos ou ambos os componentes conhecidos
	//isto e, se encontrou um duplex link que tenha um host e/ou roteador com um id desconhecidos
	void not_defined_duplex_link_points_error(String point_A, String point_B, String line)
	{
		System.out.println("Error: In input line \""+line+"\"\none or both of the endpoints for this duplex-link ("+point_A+"<--->"+point_B+") have not been declared yet.");
		System.exit(-1);
	}

	//notifica o erro que algum componente a ser configurado nao foi declarado
	void failed_to_configure_undeclared_component(char type, String line)
	{
		if(type == 'H')
			System.out.println("Error: In input line \""+line+"\"\nthe host component has not been declared yet.");
		if(type == 'R')
			System.out.println("Error: In input line \""+line+"\"\nthe router component has not been declared yet.");
		System.exit(-1);	
	}

	//notifica o erro que alguma porta ou ip esta sem par
	void missing_ip_port_par(String line)
	{
		System.out.println("Error: In input line \""+line+"\"\nthe program was expecting a relation of ports and ips of 1:1.");
		System.out.println("In case the relation is right, make sure there are no characters more than intended (say, for example, double space \' \').");
		System.exit(-1);	
	}





/*=======================================================================================*/
/*=======================================================================================*/
	//DEBUG! Todas as funcoes que estiverem daqui para baixo nao fazem parte da entrega e deverao ser apagadas!
	void print_lists()
	{
		if(hosts.isEmpty())
			System.out.println("Hosts: Empty");
		else
		{
			for(Host h : hosts)
				System.out.println("Host"+h.get_id());
		}

		if(routers.isEmpty())
			System.out.println("Routers: Empty");
		else
		{
			for(Router r : routers)
				System.out.println("Router"+r.get_id()+" with " +r.get_interfaces()+ " interfaces.");
		}

		if(duplex_links.isEmpty())
			System.out.println("DuplexLinks: Empty");
		else
		{
			for(DuplexLink dl : duplex_links)
				System.out.println("DuplexLink "+dl.get_point_A()+" <---> " +dl.get_point_B()+ " with "+dl.get_mbps_capacity()+"Mbps and "+dl.get_latency()+"ms.");
		}
	}
}

	//Print hosts
	void print_hosts()
	{
		int i = 1;
		for(Host h : hosts)
			System.out.println("Host " + i++ + ": id=" + h.id + " computer_ip=" + h.computer_ip + " router_ip=" + h.router_ip + " dns_server_ip=" + h.dns_server_ip);
	}
}
