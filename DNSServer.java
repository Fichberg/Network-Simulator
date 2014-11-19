
public class DNSServer extends Agent {
	
	private String name;
	private Node node;
	
	//Construtor
	public DNSServer(String name)
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
		return "DNSServer";
	}

}
