
public class Sniffer extends Agent {

	private String name;
	private String file;
	private DuplexLink link;
	
	//Construtor
	public Sniffer(String name)
	{
		this.name = name;
	}
	
	//setters
	public void set_file(String file)
	{
		this.file = file;
		//FALTA CRIAR O ARQUIVO TEMPORARIO AQUI
	}
	//define onde a aplicação está localizada
	public void set_residence(Object obj)
	{
		this.link = (DuplexLink) obj;
	}
	
	//getters
	public String get_name() 
	{
		return this.name;
	}
	
	public String get_type()
	{
		return "Sniffer";
	}

}
