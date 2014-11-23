import java.io.*;

public class SimulatorLogger
{
	String sniffer_name;           //Nome identificador do sniffer
	String log_file_name;          //Nome do log file deste sniffer.

	//Cria um logger
	public SimulatorLogger(String sniffer_name)
	{
		this.sniffer_name = sniffer_name;
	}

	//setter e criador do log
	public void set_log_file_name(String filename)
	{
		if(filename.isEmpty())
		{
			File single_dir = new File("logs/");
			single_dir.mkdirs();
			this.log_file_name = System.getProperty("user.dir") + "/logs/" + this.sniffer_name + ".log";
		}
		else
		{
			if(have_dir(filename))
			{
				String[] split_str = split_file_name(filename);
				String dirs = split_str[0], file = split_str[1];
				File dir = new File(dirs);
				dir.mkdirs();
			}
			this.log_file_name = System.getProperty("user.dir") + "/" + filename;
		}
	}

	//Divide a string entre o que é diretorio e o que e o file em si
	private String[] split_file_name(String file_name)
	{
		String[] split_str = new String[2];
		int split_index = -1;
		for(int i = 0; i < file_name.length(); i++)
			if(file_name.charAt(i) == '/') split_index = i;

		if(split_index != -1)
		{
			split_str[0] = file_name.substring(0, split_index + 1);
			split_str[1] = file_name.substring(split_index + 1, file_name.length());
		}
		return split_str;
	}

	//Checa se tem diretorio a ser criado
	private boolean have_dir(String file_name)
	{
		for(int i = 0; i < file_name.length(); i++)
			if(file_name.charAt(i) == '/') return true;
		return false;
	}

	//Escreve no log do sniffer. O clock recebido é o clock inicial do simulador
	void write_to_log(Clock clock, Packet packet)
	{
		String message = build_message(clock, packet);
		try 
		{
			PrintWriter log_writer = new PrintWriter(new FileWriter(this.log_file_name, true));
	        log_writer.println(message);
	        log_writer.close();
	    }
	    catch(IOException e)
	    {
	    	System.err.println("Erro de escrita do log: " + e);
			System.exit(5);
	    }
	}

	//Cria a mensagem a ser escrita no log do Sniffer
	private String build_message(Clock clock, Packet packet)
	{
		int protocol_id, upper_layers_size;
		if(packet.getTransport().get_protocol() == "TCP") 
			protocol_id = 6;
		else 
			protocol_id = 17;
		
		if(packet.getApplication() != null)
		{
			if(packet.getTransport().get_protocol() == "TCP")	
				upper_layers_size = 20 /*Desconsiderando opcoes no header do TCP*/ 
				+ packet.getApplication().get_length();
			else
				upper_layers_size = 8 /*Tamanho header minimo do UDP*/ 
				+ packet.getApplication().get_length();
		}
		else
		{
			if(packet.getTransport().get_protocol() == "TCP")	
				upper_layers_size = 20 /*Desconsiderando opcoes no header do TCP*/;
			else
				upper_layers_size = 8 /*Tamanho header minimo do UDP*/;
		}
	
		//Considerando o tamanho mínimo do header do IPv4 = 20
		String packet_id = "Packet Identification: " + packet.getId() + "\n";
		String time_elapsed = "Time Elapsed (from the start of the program execution): " 
		                     + clock.execution_time_in_milis() + "ms\n";
		String sniffer_identification = "Sniffer: " + this.sniffer_name + "\n";
		String internet_layer = "Internet Layer (IP)\n\tSource IP: " + packet.getIP_source() 
				             + "\n\tDestination IP: " + packet.getIP_destination() 
				             + "\n\tUpper Layer Protocol Identification: " + protocol_id 
				             + "\n\tPacket Length (IP header + upper layers): "
				             + " 20 + " + upper_layers_size + "\n\tTTL: "  
				             + packet.getTTL() + "\n";
		String transport_layer = null;
		if(packet.getApplication() != null)
			upper_layers_size = packet.getApplication().get_length();
		else
			upper_layers_size = 0;
		
		if(packet.getTransport().get_protocol() == "TCP")
		{
			//Considerando TCP header = 20bytes (sem opcoes). 
			//http://en.wikipedia.org/wiki/Transmission_Control_Protocol
			TCP tcp = (TCP) packet.getTransport();
			int ack = tcp.isACK() ? 1 : 0; 
			int syn = tcp.isSYN() ? 1 : 0; 
			int fin = tcp.isFIN() ? 1 : 0;
			int seq_num = tcp.getSequence_number();
			int rec_num = tcp.getACK_number();
			transport_layer = "Transport Layer (" + tcp.get_protocol() + ")\n\tSource Port: " 
			                + tcp.getSource_port() + "\n\tDestination Port: " 
					        + tcp.getDestination_port() + "\n\tPacket Length (TCP header + "
					        + "upper layer): " + " 20 + " + upper_layers_size  
					        + "\n\tSequence Number: " + seq_num + "\n\tRecognition Number: " 
					        + rec_num + "\n\tBit ACK: " + ack + "\n\tBit FIN: " + fin 
					        + "\n\tBit SYN: " + syn + "\n";
		}
		else
		{
			//Considerando UDP header = 8 bytes. 
			//http://en.wikipedia.org/wiki/User_Datagram_Protocol
			UDP udp = (UDP) packet.getTransport();
			transport_layer = "Transport Layer ("+ udp.get_protocol()+")\n\tSource Port: " 
			                + udp.getSource_port() + "\n\tDestination Port: " 
					        + udp.getDestination_port() + "\n\tPacket Length (UDP header + "
					        + "upper layer): " 
					        + " 8 + " + upper_layers_size + "\n"; 
		}
		
		String application_layer;
		if(packet.getApplication() != null)
			application_layer = "Application Layer ("+packet.getApplication().get_protocol() 
			                  +")\n\tText Data: " + packet.getApplication().get_text();
		
		else
			application_layer = "Application Layer ( )\n\tText Data: ";	

		String message = packet_id + time_elapsed + sniffer_identification + internet_layer 
			         	+ transport_layer + application_layer + ""
						+ "\n--------------------------------------------------------\n";
		System.out.println(message);
		return message;
	}

}