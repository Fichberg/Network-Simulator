import java.io.*;

public class FTPClient extends Agent {

	private String name;
	private Node node;
	
	//Construtor
	public FTPClient(String name)
	{
		this.name = name;
	}
	
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
		return "FTPClient";
	}

	public void receive_command(String command) 
	{
		// TODO Auto-generated method stub
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
	
	//alerta recebimento de pacote
	public void notify_agent(Packet packet)
	{
		
	}

	//cria um pacote com dados da camada de aplicação
	public Packet build_packet(String text, String dest_name)
	{
		ApplicationLayer app = new ApplicationLayer("FTP", text, " ", dest_name, 21, 61213);
		Packet packet = new Packet();
		packet.setApplication(app);
		return packet;
	}
	
	//processa um comando recebido do simulador e transforma-o num pacote
	public Packet process_command(String command)
	{
		String[] split = command.split(" ");
		String text = split[0] + "\r\n"
		                       + "host: " + split[1] + " \r\n";
		return build_packet(text, split[1]);
	}
}
