import java.io.*;
import java.net.*;
import java.util.*;
import java.util.regex.*;

public class InputReader
{
	//ArrayList<Host> hosts;                          //Coloca um objeto do tipo host na lista de hosts
	//ArrayList<Router> routers;                      //Coloca um objeto do tipo router na lista de routers

	//Construtor
	public InputReader(String file_name)
	{
		//this.hosts = new ArrayList<Integer>();
		//this.routers = new ArrayList<Integer>();
		read_input(file_name);
	}

	//Leitor de entrada. Responsavel por obter os dados da simulacao
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
		while(Character.isDigit(line.charAt(number_ending_index + 1)))
		{
			number_ending_index++;
			continue;
		}
		return Integer.parseInt(line.substring(number_starting_index, number_ending_index + 1));
	}
	
	//checa se a linha e de servidor. Se sim, o adiciona a hosts_array e retorna true. Caso contrario, retorna false
	boolean get_router_from_line(String line)
	{
		Pattern p = Pattern.compile("(set r)+[0-9]+ \\[\\$simulator router [0-9]+\\]+$");
    	Matcher m = p.matcher(line);
    	if(m.matches())
    	{
    		System.out.println("AQUI ROUTER " + get_number_from_line(line, 5) + ": " + line + " com " + get_number_from_line(line, 26) + " interfaces");
    		//TODO: Adicionar um objeto do tipo Router na lista de Routers aqui. User a chamada get_number_from_line(line, 5) e para isso
    		return true;
    	}
    	return false;
	}
	
	//checa se a linha e de servidor. Se sim, o adiciona a hosts_array e retorna true. Caso contrario, retorna false
	boolean get_host_from_line(String line)
	{
		Pattern p = Pattern.compile("(set h)+[0-9]+ \\[\\$simulator host\\]+$");
    	Matcher m = p.matcher(line);
    	if(m.matches())
    	{
    		System.out.println("AQUI HOST " + get_number_from_line(line, 5) + ": " + line);
    		//TODO: Adicionar um objeto do tipo Host na lista de Hosts aqui. User a chamada get_number_from_line(line, 5) para isso
    		return true;
    	}
    	return false;
	}

	//retorna o file_name com o seu path
	String file_name_with_path(String file_name)
	{
		return System.getProperty("user.dir") + "/inputs/" + file_name;
	}
}