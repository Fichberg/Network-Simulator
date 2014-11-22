import java.util.Iterator;
import java.util.LinkedList;

//Esta classe representa um buffer do router. Funciona em FIFO
public class RouterBuffer 
{
	private int enlace_nr;
	private LinkedList<Packet> buffer;
	private int max_size;
	
	//Construtor
	public RouterBuffer(int enlace_nr, int max_size) 
	{
		this.enlace_nr = enlace_nr;
		this.buffer = new LinkedList<Packet>();
		this.max_size = max_size;
	}
	
	//Getters
	public int get_enlace_nr()
	{
		return this.enlace_nr;
	}
	
	public int get_max_size()
	{
		return this.max_size;
	}
	
	//adiciona pacote à fila
	public void put_packet(Packet packet)
	{
		this.buffer.addLast(packet);
	}
	
	//tira um pacote da fila
	public Packet pull_packet()
	{
		return this.buffer.pollFirst();
	}
	
	//verifica se o buffer está cheio
	public boolean is_full() 
	{
		if (this.buffer.size() == this.max_size)
			return true;
		return false;
	}
	
	//exibe todos os pacotes que estão na fila
	public void show_packets()
	{
		Iterator<Packet> itr = this.buffer.iterator();
		while (itr.hasNext())
			System.out.println("Port " + this.enlace_nr + ": " + itr.next().getId());
	}
	
}
