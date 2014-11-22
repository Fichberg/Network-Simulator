import java.io.BufferedReader;
import java.io.FileReader;
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
		process_packet(packet);
	}
	
	//processa um pacote recebido do host
	public void process_packet(Packet packet)
	{
		String reply_address = packet.getIP_source();
		ApplicationLayer app = packet.getApplication();
		String data = process_HTTP_request(app.get_text());
		
		//configurando o header HTTP
		String text = HTTP_response(data);
		
		//configurando camada de aplicação
		app.set_dest_name(reply_address);
		app.set_dest_port(app.get_source_port());
		app.set_source_port(80);
		app.set_text(text);
		app.set_length(text.length() + data.length());
		
		//insere dados no pacote
		Packet app_pack = new Packet();
		app_pack.setApplication(app);
		app_pack.set_data(data);
		
		//envia pacote para a camada de transporte
		Host h = (Host) this.node;
		try 
		{
			h.send_TCP_packet(app_pack);
		} catch (InterruptedException e) 
		{
			System.err.println("Nao foi possivel enviar o pacote de " + this.name);
			e.printStackTrace();
		}
	}
	
	//interpreta uma requisição HTTP e responde de acordo
	private String process_HTTP_request(String text)
	{
		Pattern p = Pattern.compile("GET \\/([\\w\\.]*) HTTP");
		Matcher m = p.matcher(text);
		if (m.find())
		{
			String filename = m.group(1);
			if (filename.matches("\\s*") || filename == null)
				filename = "file.txt";
			System.out.println("é pra abrir o arquivo " + filename);
			return read_file(filename);
		}
		else
		{
			//requisição mal formada!
			return null;
		}
	}
	
	//lê um arquivo do servidor
	private String read_file(String filename)
	{
		try 
		{
			BufferedReader reader = new BufferedReader(new FileReader(filename));
			String file = reader.readLine(), line;
			while ( (line = reader.readLine()) != null)
				file += line + "\n";
			reader.close();
			return file;
		}
		catch(Exception e)
		{
			System.err.println("Erro ao tentar ler o arquivo");
			
		}
		return null;
	}
	
	//cria o cabeçalho HTTP de acordo com os dados requisitados
	private String HTTP_response(String data)
	{
		if (data == null)
			return "HTTP/1.1 404 Not Found\r\n";
		String text = "HTTP/1.1 200 OK\r\n"
				    + "Server: Apache\r\n";
		return text;
	}
}
