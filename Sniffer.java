
public class Sniffer extends Agent {

	private String name;
	private String file;
	
	//Construtor
	public Sniffer(String name)
	{
		this.name = name;
	}
	
	//setters
	public void set_file(String file)
	{
		this.file = file;
	}
	
	//getters
	public String get_name() 
	{
		return this.name;
	}
}
