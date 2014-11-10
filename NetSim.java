import java.io.*;
import java.net.*;
import java.util.*;

public class NetSim
{
	//roda o simulador
	public static void main(String[] args) throws IOException
	{
		if (check_arguments(args) == false)
			invalid_argument_notification();
		
		new InputReader(args[0]);

	}


	//Funcoes relacionadas a checagem e notificacao de entrada
	static boolean check_arguments(String[] args)
	{
		if(args.length != 1) //numero de parametros errado
			return false;
		if(!(args[0].endsWith(".txt"))) //nao tem a extensao esperada
			return false;
		return true;
	}
	static void invalid_argument_notification()
	{
		System.out.println("Invalid argument. Use:\n\njava NetSim <filename.txt>\n\nWhere filename is a .txt with the settings of the network components.");
		System.exit(-1);
	}
}

