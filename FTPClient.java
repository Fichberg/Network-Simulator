import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;


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

	//recebe comandos do simulador
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
	
	//alerta recebimento de pacote
	public void notify_agent(Packet packet)
	{
		process_FTP_response(packet);
	}

	//cria um pacote com dados da camada de aplicação
	public Packet build_packet(String text, String dest_name)
	{
		ApplicationLayer app = new ApplicationLayer("FTP", text, " ", dest_name, 21, 61213);
		Packet packet = new Packet("TCP");
		packet.setApplication(app);
		return packet;
	}
	
	//processa um comando recebido do simulador e transforma-o num pacote
	public Packet process_command(String command)
	{
		String[] split = command.split(" ");
		String text = split[0] + " FTP \r\n"
		                       + "host: " + split[1] + " \r\n";
		return build_packet(text, split[1]);
	}
	
	//processa uma resposta do servidor FTP
	private void process_FTP_response(Packet packet)
	{
		String text = packet.getApplication().get_text();
		if (text == null)
			return;
		else if (text.startsWith("226"))
			put_file_on_server(packet.getIP_source());
		else if (packet.get_data() != null && packet.get_data().length() > 1)
			save_file(packet);
	}
	
	//salva um arquivo no diretório do cliente FTP
	private void save_file(Packet packet)
	{
		File file = new File("ftp_file_received");	
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
	
	//lê um arquivo do cliente
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
	
	//envia um arquivo local para o servidor FTP
	private void put_file_on_server(String destination)
	{
		Packet packet = build_packet("NOOP\r\n", destination);
		String data = read_file("file.txt");
		packet.set_data(data);
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
}
