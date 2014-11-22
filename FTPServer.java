import java.io.BufferedReader;
import java.io.FileReader;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class FTPServer extends Agent {
	
	private String name;
	private Node node;
	
	//Construtor
	public FTPServer(String name)
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
		return "FTPServer";
	}

	public void receive_command(String command) 
	{
		// TODO Auto-generated method stub
		
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
		String data = process_FTP_request(app.get_text());
		
		//configurando o header FTP
		String text = FTP_response(data);
		
		//configurando camada de aplicação
		app.set_dest_name(reply_address);
		app.set_dest_port(app.get_source_port());
		app.set_source_port(21);
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
	
	//interpreta uma requisição FTP e responde de acordo
	private String process_FTP_request(String text)
	{
		Pattern p = Pattern.compile("GET \\S");
		Matcher m = p.matcher(text);
		Pattern p1 = Pattern.compile("USER \\S");
		Matcher m1 = p1.matcher(text);
		Pattern p2 = Pattern.compile("PUT \\S");
		Matcher m2 = p2.matcher(text);
		if (m.find())
		{
			String filename = m.group(1);
			if (filename.matches("\\s*") || filename == null)
				filename = "index.html";
			System.out.println("é pra enviar o arquivo " + filename);
			return read_file(filename);
		}
		else if(m1.find())
		{
			String username = m.group(1);
			if (username.matches("\\s*") || username == null)
				username = "Anonymous";
			System.out.println("é para saudar o usuario " + username);
			return username;
		}
		else if(m2.find())
		{
			String filename = m.group(1);
			System.out.println("é pra receber o arquivo " + filename);
			return read_file(filename);
		}
		return null;
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
	
	//cria o cabeçalho FTP de acordo com os dados requisitados
	private String FTP_response(String data)
	{
		if (data == null)
			return "500 Unknown command\r\n";
		else if (data.startsWith("USER"))
			return "220 Welcome.\r\n";
		else
			return "200 OK\r\n";
	}
}
