
public class Sniffer extends Agent {

	private String name;                 //Nome do sniffer
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
	
	//método que alerta o sniffer sobre a transmissão de um pacote no link
	public void notify_agent(Packet packet)
	{
		//write_capture(clock, packet, print);
	}

	//funcao que escreve o log das capturas deste sniffer. Escreve em que tempo de execucao (em relacao ao inicio) a captura do pacote packet ocorreu.
	//O booleano indica se e para imprimir no terminal. Ainda nao decidido se vai se manter na versao final  
	public void write_capture(Packet packet)
	{
		this.snifferLog.write_to_log(this.clock, packet);
	}

	//funcao que imprime o conteudo do log deste sniffer no prompt. Esta funcao roda apenas ao terminar a aplicacao.
	public void read_captures()
	{
		this.snifferLog.read_from_log();
	}
}
