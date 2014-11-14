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
	public InputReader(String file_name)
	{
		this.hosts = new ArrayList<Host>();
		this.routers = new ArrayList<Router>();
		this.duplex_links = new ArrayList<DuplexLink>();
		read_input(file_name);
	}

	//Leitor de entrada. Responsável por obter os dados da simulação
	void read_input(String file_name)
	{
		File file = new File(file_name = file_name_with_path(file_name));
		BufferedReader file_reader = null;

		try
		{
    		file_reader = new BufferedReader(new FileReader(file));
    		for(String line = file_reader.readLine(); line != null; line = file_reader.readLine())
    		{
    			if(get_host_from_line(line) == true) continue;
    			else if(get_router_from_line(line) == true) continue;
    			else if(get_duplex_link_from_line(line)) continue;


    			//TODO: terminar os tratamentos das linhas de entrada.
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
	boolean get_duplex_link_from_line(String line)
	{
		Pattern p = Pattern.compile("(\\$simulator duplex-link \\$){1}?((h|r)[0-9]+){1}?(\\.[0-9])?( \\$){1}?((h|r)[0-9]){1}?(\\.[0-9])?( [0-9]+Mbps [0-9]+ms){1}?$");
    	Matcher m = p.matcher(line);
    	if(m.matches())
    	{
    		String point_A, point_B;
    		float mbps_capacity, latency;

    		point_A = get_substring_from_line(line.substring(1), line.substring(1).indexOf("$") + 1);
    		point_B = get_substring_from_line(line, line.lastIndexOf("$") + 1);
   			mbps_capacity = get_number_from_line(line.substring(0, line.lastIndexOf(' ')), line.substring(0, line.lastIndexOf(' ')).lastIndexOf(' ') + 1);
    		latency = get_number_from_line(line, line.lastIndexOf(' ') + 1);

    		//Os hosts/routers precisam estar na lista
    		if(!check_points_availability(point_A, point_B))
    			not_defined_duplex_link_points_error(point_A, point_B, line);

    		//Insere na lista
    		duplex_links.add(new DuplexLink(point_A, point_B, mbps_capacity, latency));
    		return true;
    	}
    	return false;
	}

	//checa se os pontos existem de fato para que este link exista
	boolean check_points_availability(String point_A, String point_B)
	{
		int point_A_id, point_B_id;
		if(point_A.charAt(0) == 'h')
			point_A_id = Integer.parseInt(point_A.substring(1));
		else
			point_A_id = Integer.parseInt(point_A.substring(1).substring(0, point_A.substring(1).indexOf('.')));
		if(point_B.charAt(0) == 'h')
			point_B_id = Integer.parseInt(point_B.substring(1));
		else
			point_B_id = Integer.parseInt(point_B.substring(1).substring(0, point_B.substring(1).indexOf('.')));

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
		for(ending_index = starting_index; !Character.isSpaceChar(line.charAt(ending_index)); ending_index++) continue;
		return line.substring(starting_index, ending_index);
	}

	//checa se a linha e de router. Se sim, o adiciona a routers e retorna true. Caso contrario, retorna false
	boolean get_router_from_line(String line)
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
    		return true;
    	}
    	return false;
	}

	//checa se a linha e de host. Se sim, o adiciona a hosts e retorna true. Caso contrario, retorna false
	boolean get_host_from_line(String line)
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
    		return true;
    	}
    	return false;
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

	//retorna o file_name com o seu path
	String file_name_with_path(String file_name)
	{
		return System.getProperty("user.dir") + "/inputs/" + file_name;
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
