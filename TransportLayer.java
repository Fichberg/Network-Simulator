
public abstract class TransportLayer 
{
	String protocol;
	int source_port;
	int destination_port;
	int length;
	
	public abstract String get_protocol();
	public abstract void clone_to(TransportLayer tl);

}
