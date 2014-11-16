import java.util.*;
import java.io.*;

public class Router
{
	private int id;                           //identificador único do router. 
	private int interfaces;                   //número de interfaces que este router tem.
	private HashMap<String, Integer> ip_port; //dicionário IP -> interface associada.

	//Construtor
	public Router(int id, int interfaces)
	{
		this.id = id;
		this.interfaces = interfaces;
		this.ip_port = new HashMap<String, Integer>();
	}

	/*"setters" aqui são desnecessários, pois a configuração é ditada pelo arquivo
	de entrada e é imutável. */

	//getters
	public int get_id()
	{
		return this.id;
	}

	public int get_interfaces()
	{
		return this.interfaces;
	}
	
	//Funções para manipulação do atributo ip_port
	//////////////////////////////////////////////////////////////////////////////////////
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
