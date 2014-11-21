import java.util.regex.Matcher;
import java.util.regex.Pattern;


public class HTTPServer extends Agent {
	
	private String name;
	private Node node;
	
	//Construtor
	public HTTPServer(String name)
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
		return "HTTPServer";
	}
	
	//=========================================
	// COMMUNICATION
	
	//recebe comando do servidor
	public void receive_command(String command) 
	{
		
	}
	
	//alerta recebimento de pacote
	public void notify_agent(Packet packet)
	{
		
	}
	
	//processa um pacote recebido do host
	public void process_packet(Packet packet)
	{
		String reply_address = packet.getIP_source();
		
	}
	
	//interpreta uma requisição HTTP e responde de acordo
	private String process_HTTP_request(String text)
	{
		Pattern p = Pattern.compile("GET \\/([\\w\\.]*) HTTP");
		Matcher m = p.matcher(text);
		if (m.find())
		{
			
			m.group(1);
		}
		else
		{
			//requisição mal formada!
		}
	}
	
	//lê um arquivo do servidor
	private String read_file(String filename)
	{
		
	}
}
