import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
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
		//Método da classe abstrata: não utilizado
	}
	
	//alerta recebimento de pacote
	public void notify_agent(Packet packet)
	{
		if (packet.get_data() != null && packet.get_data().length() > 1)
			save_file(packet);
		else
			process_packet(packet);
	}

	//processa um pacote recebido do host
	public void process_packet(Packet packet)
	{
		String reply_address = packet.getIP_source();
		ApplicationLayer app = packet.getApplication();
		String data = process_FTP_request(app.get_text());
		
		//configurando o header FTP
		String text = FTP_response(app.get_text());
		
		//configurando camada de aplicação
		app.set_dest_name(reply_address);
		app.set_dest_port(app.get_source_port());
		app.set_source_port(21);
		app.set_text(text);
		app.set_length(text.length());
		
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
	
	//interpreta uma requisição FTP por arquivo e responde de acordo
	private String process_FTP_request(String text)
	{
		Pattern p  = Pattern.compile("GET FTP");
		Matcher m  = p.matcher(text);

		if (m.find())
			return read_file("file.txt");
		return "";
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
	
	//salva um arquivo recebido por PUT
	private void save_file(Packet packet)
	{
		File file = new File("ftps_file_received");	
		try 
		{
			BufferedWriter bw = new BufferedWriter(new FileWriter(file));
			bw.write(packet.get_data());
			bw.close();
		} 
		catch (IOException e) 
		{
			e.printStackTrace();
		}
	}
	
	//cria o cabeçalho FTP de acordo com os dados requisitados
	private String FTP_response(String data)
	{
		Pattern p1  = Pattern.compile("USER");
		Matcher m1  = p1.matcher(data);
		Pattern p2  = Pattern.compile("PUT");
		Matcher m2  = p2.matcher(data);
		
		if (data == null)
			return "500 Unknown command\r\n";
		else if (m1.find())
			return "220 Welcome.\r\n";
		else if (m2.find())
			return "226 OK\r\n";
		else
			return "200 OK\r\n";
	}
}
