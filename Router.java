import java.util.*;
import java.io.*;

public class Router extends Node
{
	private String name;                      //identificador único do router. 
	private int interfaces;                   //número de interfaces que este router tem.
	private float performance;                //tempo para processar 1 pacote em ms;
	private HashMap<String, Integer> ip_port; //dicionário IP -> interface associada.
	private HashMap<String, String> routes;   //tabela de roteamento do router.
	private HashMap<Integer, String> buffers; //filas dos pacotes associadas a uma interface

	//Construtor
	public Router(String name, int interfaces)
	{
		this.name = name;
		this.interfaces = interfaces;
		this.ip_port = new HashMap<String, Integer>();
		this.routes = new HashMap<String, String>();
		this.buffers = new HashMap<Integer, String>();
	}

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
	
	/******** ATENÇÃO: esta definição é temporária! ***********/
	public void set_buffer_size(int port, String size)
	{
		this.buffers.put(port, size);
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


}
