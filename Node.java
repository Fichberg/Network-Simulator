/*Esta classe representa um nó da rede, que pode ser tanto um host quanto um router*/
public abstract class Node {

	private String name;
	
	public String get_name()
	{
		return this.name;
	}
}
