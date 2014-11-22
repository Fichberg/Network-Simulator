import java.io.*;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Timer;
import java.util.TimerTask; 

public class NetSim
{
	//roda o simulador
	public static void main(String[] args) throws IOException
	{
		if (check_arguments(args) == false)
			invalid_argument_notification();
		
		Clock clock = new Clock();
		InputReader ir = new InputReader(args[0], clock);
		init_all_nodes(ir);
		
		//inicia o relógio do simulador
		start_countdown();
		Timer timer = new Timer(true);
		schedule(ir, timer);
		
	}

	//====================================
	//UTILS
	
	//agendador de disparos de comandos para os agentes
	private static void schedule(InputReader ir, Timer timer)
	{
		HashMap<Float, String> commands = ir.get_simulator_program();
		Iterator<Float> itr = commands.keySet().iterator();
		while(itr.hasNext())
		{
			//disparo indica o tempo em que será executado um comando
			float disparo  = itr.next();
			String command = commands.get(disparo);
			String[] split = command.split(" ", 2);
			
			//agent indica qual agente será agendado
			String agent   = split[0];  
			if (agent.equalsIgnoreCase("finish"))
			{
				//caso excepcional: fim do programa
				schedule_finish((long)disparo, timer);
				continue;
			}
			
			//message indica qual a mensagem enviada ao agente
			final String message = split[1];
			final Agent a  = ir.get_agent(agent);
			TimerTask tt = new TimerTask() 
			{
				public void run() 
				{
					a.receive_command(message);
				}
			};
			timer.schedule(tt, (long)(disparo * 1000));		
		}
	}

	//agenda o fim do programa, com um timer e um disparo em segs
	private static void schedule_finish(Long disparo, Timer timer)
	{
		TimerTask tt = new TimerTask() 
		{
			public void run() 
			{
				System.out.println("System shutting down NOW...");
				System.exit(0);
			}
		};
		timer.schedule(tt, disparo * 1000);
	}
	
	//pretty-printing da inicialização do simulador :)
	private static void start_countdown()
	{
		try 
		{
			System.out.print("Initializing simulation in 3...");
			Thread.sleep(250);
			System.out.print("\rInitializing simulation in 3...2...");
			Thread.sleep(250);
			System.out.println("\rInitializing simulation in 3...2...1...");
			Thread.sleep(250);		
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
	}
	
	//roda todos os hosts e routers da simulação
	private static void init_all_nodes(InputReader ir)
	{
		Node[] nodes = ir.get_all_nodes();
		for (Node n : nodes)
		{
			//System.out.println("Initializing node "+ n.get_name());
			n.start();
		}
	}
	
	
	//====================================
	//ERROR CHECKING
	
	//checa argumentos de entrada
	static private boolean check_arguments(String[] args)
	{
		if(args.length != 1) //número errado de parâmetros
			return false;
		if(!(args[0].endsWith(".txt"))) //não tem extensão esperada
			return false;
		return true;
	}
	
	//notifica erro e termina execução do programa
	static private void invalid_argument_notification()
	{
		System.err.println("Invalid argument. Use:\n\njava NetSim <filename.txt>\n\nWhere "
						 + "filename.txt contains the network components settings.");
		System.exit(-1);
	}
}

