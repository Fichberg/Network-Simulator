/*Esta classe representa um nó da rede, que pode ser tanto um host quanto um router*/
public abstract class Node extends Thread {

	public abstract String get_name();

	public abstract void set_link(DuplexLink link, String port);
	
	public abstract void receive_packet(DuplexLink link, Packet packet);
	
	@Override
	public abstract void run();
}
