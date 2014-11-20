import java.io.*;
import java.util.logging.*;

public class Sniffer extends Agent {

	private String name;                 //Nome do sniffer
	private String file_name;            //Nome do log file deste sniffer. <PATH do diretorio em que esta o NetSim>/logs/<Nome do sniffer>.log
	private DuplexLink link;
	private Logger snifferLog;           //Logger
	
	//Construtor
	public Sniffer(String name)
	{
		this.name = name;
		this.file_name = System.getProperty("user.dir") + "/logs/" + name + ".log";
		this.snifferLog = Logger.getLogger(name + "Log");
		create_log();
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

	//funcao para criar o log que contera as informacoes das capturas de pacotes
	public void create_log()
	{
		try
		{
			this.snifferLog.setLevel(Level.FINE);
			FileHandler fh = new FileHandler(this.file_name, true);
			this.snifferLog.addHandler(fh);
			SimpleFormatter formatter = new SimpleFormatter();
			fh.setEncoding("UTF-8");
			fh.setFormatter(formatter);
		} 
		catch (IOException logger) 
		{
			System.err.println("Sniffer.java:51: failed to create a logger to \"" + this.name + "\": " + this.snifferLog);
		}
	}

	//funcao que escreve o log das capturas deste sniffer
	public void write_capture(String message)
	{
		this.snifferLog.fine(message);
	}

	//funcao que imprime o conteudo do log no prompt deste sniffer. Esta funcao roda apenas ao terminar a aplicacao.
	public void read_captures()
	{
		try
		{
	    	FileReader inputFile = new FileReader(this.file_name);
		    BufferedReader bufferReader = new BufferedReader(inputFile);
		    String line;
		    while ((line = bufferReader.readLine()) != null)
         	   System.out.println(line);
		    bufferReader.close();
    	}
    	catch(Exception e)
    	{
            System.err.println("Sniffer.java:75: Error while reading file:" + e.getMessage());                      
  	  	}
	}

	public void receive_command(String command) 
	{
		// TODO Auto-generated method stub
		
	}
}
