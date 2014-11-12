public class Router
{
	int id;             //identificador do router. Este identificador é único para este router
	int interfaces;     //numero de interfaces que este router tem

	//Construtor
	public Router(int id, int interfaces)
	{
		this.id = id;
		this.interfaces = interfaces;
	}

	//setters desnecessarios pois a configuracao e imutavel e ja escolhida no arquivo de entrada.

	//getters
	public int get_id()
	{
		return this.id;
	}

	public int get_interfaces()
	{
		return this.interfaces;
	}
}