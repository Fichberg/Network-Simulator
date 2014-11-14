public class Router
{
	private int id;         //identificador do router. Este identificador é único 
	private int interfaces; //número de interfaces que este router tem

	//Construtor
	public Router(int id, int interfaces)
	{
		this.id = id;
		this.interfaces = interfaces;
	}

	/*"setters" aqui são desnecessários, pois a configuração é ditada pelo arquivo
	de entrada e é imutável. */

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