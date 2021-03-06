
public class Sniffer extends Agent {

	private String name;                 //Nome do sniffer
	@SuppressWarnings("unused")
	private DuplexLink link;
	private SimulatorLogger snifferLog;  //Logger
	private Clock clock;                 //Guarda o tempo inicial de execucao
	
	//Construtor
	public Sniffer(String name, Clock clock)
	{
		this.clock = clock;
		this.name = name;
		this.snifferLog = new SimulatorLogger(name);
	}
	
	//define onde a aplicação está localizada
	public void set_residence(Object obj)
	{
		this.link = (DuplexLink) obj;
	}

	//setta o nome do arquivo de saída no log do sniffer
	public void set_filename(String filename)
	{
		this.snifferLog.set_log_file_name(filename);
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

	//O Sniffer não faz nada com isso. É o preço a pagar por usar classes abstratas
	public void receive_command(String command)
	{

	}
	
	//O Sniffer não faz nada com isso. É o preço a pagar por usar classes abstratas
	public void notify_agent(Packet packet)
	{

	}

	//funcao que escreve o log das capturas deste sniffer. Escreve em que tempo de execucao 
	//(em relacao ao inicio) a captura do pacote packet ocorreu.
	public void write_capture(Packet packet)
	{
		this.snifferLog.write_to_log(this.clock, packet);
	}

}
