import java.util.*;
import java.io.*;

public class Router
{
	int id;                       //identificador do router. Este identificador é único para este router
	int interfaces;               //numero de interfaces que este router tem
	String ports_and_ips;         //String com os pares de ip / porta.

	//Construtor
	public Router(int id, int interfaces)
	{
		this.id = id;
		this.interfaces = interfaces;
	}

	//setters desnecessarios pois a configuracao e imutavel e ja escolhida no arquivo de entrada.

	//getters
	public int get_id()
	{
		return this.id;
	}

	public int get_interfaces()
	{
		return this.interfaces;
	}

/*******************************************************************************************************************************************************************/
	//Funcoes para a manipulacao do atributo String ports_and_ips
	//Checa se a porta existe no atributo de portas e strings
	boolean have_port(String port)
	{
		String all_ports_and_ips = " " + ports_and_ips;
		int number_of_spaces = char_counter(' ', all_ports_and_ips);
		for(int i = 1; i <= number_of_spaces; i += 2) //i pode ir de 2 em 2, ja que estamos procurando portas e nao IPs
		{
			String port_from_string;
			if(i < number_of_spaces)
				port_from_string = all_ports_and_ips.substring(get_nth_char_index(all_ports_and_ips, ' ', i) + 1, get_nth_char_index(all_ports_and_ips, ' ', i + 1));
			else
				port_from_string = all_ports_and_ips.substring(get_nth_char_index(all_ports_and_ips, ' ', i) + 1);
			if(port_from_string.compareTo(port) == 0) return true;
		}
		return false;
	}

	//Checa se o IP existe no atributo de portas e strings
	boolean have_ip(String ip)
	{
		// 0 192.168.3.4 1 192.168.2.3 2 192.168.1.2
		String all_ports_and_ips = " " + ports_and_ips;
		int number_of_spaces = char_counter(' ', all_ports_and_ips);
		for(int i = 2; i <= number_of_spaces; i += 2) //i pode ir de 2 em 2, ja que estamos procurando IPs e nao portas
		{
			String ip_from_string;
			if(i < number_of_spaces)
				ip_from_string = all_ports_and_ips.substring(get_nth_char_index(all_ports_and_ips, ' ', i) + 1, get_nth_char_index(all_ports_and_ips, ' ', i + 1));
			else
				ip_from_string = all_ports_and_ips.substring(get_nth_char_index(all_ports_and_ips, ' ', i) + 1);
			if(ip_from_string.compareTo(ip) == 0) return true;
		}
		return false;
	}

	//Busca o IP associado a porta
	String retrieve_associated_ip(String port)
	{
		String all_ports_and_ips = " " + ports_and_ips;
		int number_of_spaces = char_counter(' ', all_ports_and_ips), i;
		boolean found = false;
		for(i = 1; i <= number_of_spaces; i += 2) //i pode ir de 2 em 2, ja que estamos procurando a partir do IP
		{
			String port_from_string;
			if(i < number_of_spaces)
				port_from_string = all_ports_and_ips.substring(get_nth_char_index(all_ports_and_ips, ' ', i) + 1, get_nth_char_index(all_ports_and_ips, ' ', i + 1));
			else
				port_from_string = all_ports_and_ips.substring(get_nth_char_index(all_ports_and_ips, ' ', i) + 1);
			if(port_from_string.compareTo(port) == 0) 
			{
				found = true; break;
			}
		}
		if(found == true)
		{
			if(i < number_of_spaces - 1)
				return all_ports_and_ips.substring(get_nth_char_index(all_ports_and_ips, ' ', i + 1) + 1, get_nth_char_index(all_ports_and_ips, ' ', i + 2));
			else
				return all_ports_and_ips.substring(get_nth_char_index(all_ports_and_ips, ' ', i + 1) + 1);
		}
		else
			return null;
	}

	//Busca a porta associado ao IP
	String retrieve_associated_port(String ip)
	{
		String all_ports_and_ips = " " + ports_and_ips;
		int number_of_spaces = char_counter(' ', all_ports_and_ips), i;
		boolean found = false;
		for(i = 2; i <= number_of_spaces; i += 2) //i pode ir de 2 em 2, ja que estamos procurando a partir do IP
		{
			String ip_from_string;
			if(i < number_of_spaces)
				ip_from_string = all_ports_and_ips.substring(get_nth_char_index(all_ports_and_ips, ' ', i) + 1, get_nth_char_index(all_ports_and_ips, ' ', i + 1));
			else
				ip_from_string = all_ports_and_ips.substring(get_nth_char_index(all_ports_and_ips, ' ', i) + 1);
			if(ip_from_string.compareTo(ip) == 0) 
			{
				found = true; break;
			}
		}
		if(found == true)
			return all_ports_and_ips.substring(get_nth_char_index(all_ports_and_ips, ' ', i - 1) + 1, get_nth_char_index(all_ports_and_ips, ' ', i));
		else
			return null;
	}

/*******************************************************************************************************************************************************************/
	//Metodos privados
	//Retorna o indice da n-esima ocorrencia nth_char do caracter ch na string line. -1 se nao encontrar o caracter com o numero ocorrencias procurado
	private int get_nth_char_index(String line, char ch, int nth_char)
	{
		int counter = 0;
		for(int i = 0; i < line.length(); i++)
		{
			if(line.charAt(i) == ch) counter++;
			if(counter == nth_char) return i;
		}
		return -1;
	}

	//Conta o numero de ocorrencias de um caracter ch na string line
	private int char_counter(char ch, String line)
	{
		int counter = 0, i;
		for(i = 0; i < line.length(); i++)
			if(line.charAt(i) == ch) counter++;
		return counter;
	}
}