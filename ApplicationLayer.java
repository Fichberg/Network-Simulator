
public class ApplicationLayer {
	
	private String protocol;    //protocolo da camada de aplicação
	private String text;        //mensagem contida no cabeçalho
	private int length;         //tamanho total da camada de aplicação
	private int data_length;    //tamanho total dos dados
	
	//Dados para a camada de transporte ---------------------------
	private String dest_name;  //nome do destino
	private int dest_port;     //porta de destino
	private int source_port;   //porta fonte 

	//Construtor
	public ApplicationLayer(String protocol, String text, String data, String dest_name,
			int dest_port, int source_port) 
	{
		this.protocol = protocol;
		this.text = text;
		this.dest_name = dest_name;
		this.data_length = data.length();
		this.length = this.data_length + text.length();
		this.dest_port = dest_port;
		this.source_port = source_port;
	}
	
	//getters
	public String get_dest_name() 
	{
		return this.dest_name;
	}
	
	public int get_dest_port()
	{
		return this.dest_port;
	}
	
	public int get_source_port()
	{
		return this.source_port;
	}
	
	public int get_length()
	{
		return this.length;
	}
	
	public String get_text()
	{
		return this.text;
	}
	
	public String get_protocol()
	{
		return this.protocol;
	}
	
	//setters
	public void set_text(String text)
	{
		this.text = text;
	}
	
	public void set_length(int length)
	{
		this.length = length;
	}
	
	public void set_dest_port(int dest_port)
	{
		this.dest_port = dest_port;
	}
	
	public void set_source_port(int source_port)
	{
		this.source_port = source_port;
	}
	
	public void set_dest_name(String dest_name)
	{
		this.dest_name = dest_name;
	}
}
