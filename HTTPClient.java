
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
		//DEBUG! -- APAGAR NO FUTURO
		System.out.println("Recebi o comando: " + command);
	}
	
	
}
