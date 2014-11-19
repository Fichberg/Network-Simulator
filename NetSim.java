import java.io.*;


public class NetSim
{
	//roda o simulador
	public static void main(String[] args) throws IOException
	{
		if (check_arguments(args) == false)
			invalid_argument_notification();
		
		new InputReader(args[0]);
		
	}

	//Funções relacionadas a verificação e notificação de entrada
	static boolean check_arguments(String[] args)
	{
		if(args.length != 1) //número errado de parâmetros
			return false;
		if(!(args[0].endsWith(".txt"))) //não tem extensão esperada
			return false;
		return true;
	}
	static void invalid_argument_notification()
	{
		System.err.println("Invalid argument. Use:\n\njava NetSim <filename.txt>\n\nWhere "
						 + "filename.txt contains the network components settings.");
		System.exit(-1);
	}
}

