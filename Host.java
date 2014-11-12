public class Host
{
	int id;             //identificador do host. Este identificador é único para este host

	//Construtor
	public Host(int id)
	{
		this.id = id;
	}

	//setters desnecessarios pois a configuracao e imutavel e ja escolhida no arquivo de entrada.

	//getters
	public int get_id()
	{
		return this.id;
	}
}