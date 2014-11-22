import java.io.*;


public class HTTPClient extends Agent {
	
	private String name;
	private Node node;
	
	//Construtor
	public HTTPClient(String name)
	{
		this.name = name;
	}
	
	
	//=======================================
	//GETTER AND SETTERS
	
	//define onde a aplicação está localizada
	public void set_residence(Object obj)
	{
		this.node = (Node) obj;
	}
	
	//getters
	public String get_name() 
	{
		return this.name;
	}
	
	public String get_type()
	{
		return "HTTPClient";
	}
	
	//======================================
	//SIMULATOR
	
	//recebe comando
	public void receive_command(String command)
	{
		Packet packet = process_command(command);
		Host h = (Host) this.node;
		try
		{
			h.send_TCP_packet(packet);
		}
		catch(InterruptedException e)
		{
			System.err.println("Pacote " + packet.getId() + " nao pode ser enviado"
					         + "por " + this.name);
			e.printStackTrace();
		}
	}
	
	//======================================
	//COMMUNICATION
	
	//alerta recebimento de pacote
	public void notify_agent(Packet packet)
	{
		System.out.println("recebi a resposta com o arquivo: " + packet.get_data());
	}
	
	//cria um pacote com dados da camada de aplicação
	public Packet build_packet(String text, String dest_name)
	{
		ApplicationLayer app = new ApplicationLayer("HTTP", text, " ", dest_name, 80, 54823);
		Packet packet = new Packet("TCP");
		packet.setApplication(app);
		return packet;
	}
	
	//processa um comando recebido do simulador e transforma-o num pacote
	public Packet process_command(String command)
	{
		String[] split = command.split(" ");
		String text = split[0] + " / HTTP/1.1 \r\n"
		                       + "host: " + split[1] + " \r\n";
		return build_packet(text, split[1]);
	}	
	
}
