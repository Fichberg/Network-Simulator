import java.io.*;
import java.util.logging.*;

public class Sniffer extends Agent {

	private String name;                 //Nome do sniffer
	private DuplexLink link;
	private SimulatorLogger snifferLog;  //Logger
	
	//Construtor
	public Sniffer(String name)
	{
		this.name = name;
		this.snifferLog = new SimulatorLogger(name);
	}
	
	//define onde a aplicação está localizada
	public void set_residence(Object obj)
	{
		this.link = (DuplexLink) obj;
	}
	
	//getters
	public String get_name() 
	{
		return this.name;
	}
	
	public String get_type()
	{
		return "Sniffer";
	}
/*-----------------------------------------------------------------------------------------------------------------------*/
// O que fazer com este metodo nesta classe?! Este metodo foi adicionado em Agent.java!
	public void receive_command(String command)
	{
		System.out.println("TODO: ME OLHE NA LINHA 36 de Sniffer.java!");
	}
/*-----------------------------------------------------------------------------------------------------------------------*/

	//funcao que escreve o log das capturas deste sniffer. Escreve em que tempo de execucao (em relacao ao inicio) a captura do pacote packet ocorreu.
	//O booleano indica se e para imprimir no terminal. Ainda nao decidido se vai se manter na versao final  
	public void write_capture(Clock clock, Packet packet, boolean print)
	{
		this.snifferLog.write_to_log(clock, packet, print);
	}

	//funcao que imprime o conteudo do log deste sniffer no prompt. Esta funcao roda apenas ao terminar a aplicacao.
	public void read_captures()
	{
		this.snifferLog.read_from_log();
	}
}
