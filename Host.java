public class Host
{
	private int id;  //identificador do host. Este identificador é único.

	//Construtor
	public Host(int id)
	{
		this.id = id;
	}

	/*"setters" aqui são desnecessários, pois a configuração é ditada pelo arquivo
	de entrada e é imutável. */

	//getters
	public int get_id()
	{
		return this.id;
	}
}
