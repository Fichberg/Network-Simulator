import java.io.*;

public class SimulatorLogger
{
	String sniffer_name;           //Nome identificador do sniffer
	String log_file_name;          //Nome do log file deste sniffer. <PATH do diretorio em que esta o NetSim>/logs/<Nome do sniffer>.log

	//Cria um logger
	public SimulatorLogger(String sniffer_name)
	{
		this.sniffer_name = sniffer_name;
		this.log_file_name = System.getProperty("user.dir") + "/logs/" + sniffer_name + ".log";
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

	void read_from_log()
	{
		try
		{
	    	FileReader inputFile = new FileReader(this.log_file_name);
		    BufferedReader bufferReader = new BufferedReader(inputFile);
		    String line;
		    while ((line = bufferReader.readLine()) != null)
         	   System.out.println(line);
		    bufferReader.close();
    	}
    	catch(Exception e)
    	{
            System.err.println("Sniffer.java:48: Error while reading file: " + e.getMessage());                      
  	  	}
	}

	//Cria a mensagem a ser escrita no log do Sniffer
	private String build_message(Clock clock, Packet packet)
	{
		//TODO: ACERTAR OS CAMPOS COM ???
		int protocol_id;
		if(packet.getTransport().protocol == "TCP") protocol_id = 6;
		else protocol_id = 17;
		int upper_layers_size = packet.getTransport().length + packet.getApplication().get_length();
		String packet_id = "Packet Identification: " + packet.getId() + "\n";
		String time_elapsed = "Time Elapsed (from the start of the program execution): " + clock.execution_time_in_milis() + "ms\n";
		String sniffer_identification = "Sniffer: " + this.sniffer_name + "\n";
		String internet_layer = "Internet Layer (IP)\n\tSource IP: " + packet.getIP_source() + "\n\tDestination IP: " + 
		packet.getIP_destination() + "\n\tUpper Layer Protocol Identification: " + protocol_id + "\n\tPacket Length (this layer + upper layers): " + 
		packet.getLength() + " + " + upper_layers_size + "\n\tTTL: " + packet.getTTL() + "\n";
		String transport_layer;
		upper_layers_size = packet.getApplication().get_length();
		if(packet.getTransport().protocol== "TCP")
		{
			transport_layer = "Transport Layer ("+packet.getTransport().protocol+")\n\tSource Port: " + packet.getTransport().source_port + 
			"\n\tDestination Port: " + packet.getTransport().destination_port + "\n\tPacket Length (this layer + upper layer): " + 
			packet.getTransport().length + " + " + upper_layers_size + "\n\tSequence Number: " + "???" + "\n\tRecognition Number: " + 
			"???" + "\n\tBit ACK: " + "???" + "\n\tBit FIN: " + "???" + "\n\tBit SYN: " + "???" + "\n";
		}
		else
		{
			transport_layer = "Transport Layer ("+packet.getTransport().protocol+")\n\tSource Port: " + packet.getTransport().source_port + 
			"\n\tDestination Port: " + packet.getTransport().destination_port + "\n\tPacket Length (this layer + upper layer): " + 
			packet.getTransport().length + " + " + upper_layers_size + "\n";
		}
		String application_layer = "Application Layer ("+packet.getApplication().get_protocol() +")\n\tText Data: " + packet.getApplication().get_text();

		return packet_id + time_elapsed + sniffer_identification + internet_layer + transport_layer + application_layer + "\n\n";
	}

}