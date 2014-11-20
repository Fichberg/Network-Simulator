
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

	public void receive_command(String command) 
	{
		// TODO Auto-generated method stub
		
	}
}
