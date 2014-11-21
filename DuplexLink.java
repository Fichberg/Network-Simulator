
public class DuplexLink extends Thread
{
	/* point_A e point_B guardam quem são os componentes da rede dos extremos
	   deste duplex-link. Os extremos podem ser tanto hosts quanto routers, e
	   são armazenadas com o hX, ou rY.P {P é a porta e X e Y são os
	   identificadores do componente}.*/
	private Node point_A;
	private Node point_B;
	private float capacity;  //capacidade deste duplex-link em MBps.
	private float latency;   //a latência deste duplex-link em ms.
	private Sniffer sniffer; //sniffer associado ao link.

	//Construtor
	public DuplexLink(Node point_A, Node point_B, float capacity, float latency)
	{
		this.point_A = point_A;
		this.point_B = point_B;
		this.capacity = capacity;
		this.latency = latency;
		this.sniffer = null;
	}

	//setters
	public void set_sniffer(Sniffer sniffer)
	{
		this.sniffer = sniffer;
	}

	//getters
	public Node get_point_A()
	{
		return this.point_A;
	}

	public Node get_point_B()
	{
		return this.point_B;
	}

	public float get_mbps_capacity()
	{
		return this.capacity;
	}

	public float get_latency()
	{
		return this.latency;
	}
	
	//conecta-se aos extremos do enlace passando a si mesmo
	public void set_link(String point_A, String point_B)
	{
		String A = null, B = null;
		String[] splitA = point_A.split("\\.");
		String[] splitB = point_B.split("\\.");
		if (splitA.length == 2)
			A = splitA[1];
		if (splitB.length == 2)
			B = splitB[1];
		this.point_A.set_link(this, A);
		this.point_B.set_link(this, B);
	}

	//verifica se o link contém as duas pontas
	public boolean has_edges(String point_A, String point_B)
	{
		String []split_str;
		if(point_A.charAt(0) == 'r')
		{
			split_str = point_A.split("\\.", 2);
			point_A = split_str[0];
		}
		if(point_B.charAt(0) == 'r')
		{
			split_str = point_B.split("\\.", 2);
			point_B = split_str[0];
		}
		String edge_one = this.point_A.get_name();
		String edge_two = this.point_B.get_name();
		if ( (edge_one.equals(point_A) && edge_two.equals(point_B)) ||
			 (edge_two.equals(point_A) && edge_one.equals(point_B)))
			return true;
		return false;
	}
	
	//devolve um Node associado a um nome
	private Node get_Node(String point) 
	{
		Node ponto = null;
		if (this.point_A.get_name().equals(point))
			ponto = this.point_A;
		else if(this.point_B.get_name().equals(point))
			ponto = this.point_B;
		else
			System.err.println("Inexistant point");
		return ponto;
	}
	
	//verifica se determinado ponto é de host
	public boolean is_host_point(String point)
	{
		Node ponto = get_Node(point);
		if (ponto instanceof Host)
			return true;
		return false;
	}

	//verifica se determinado ponto é de router
	public boolean is_router_point(String point)
	{
		Node ponto = get_Node(point);
		if (ponto instanceof Router)
			return true;
		return false;
	}
	
	//============================================
	//COMMUNICATION
	
	//passa pra frente o pacote de um nó para outro
	public void forward_packet(Node sender, Packet packet)
	{
		//COLOCAR A LATENCIA AQUI!!!!!!!!!!
	/*	try {
			sleep(100);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}*/
		Node receiver = null;
		if (sender.equals(this.point_A))
			receiver = this.point_B;
		else if (sender.equals(this.point_B))
			receiver = this.point_A;
		else
		{
			System.err.println("You can't send packets if you don't belong to me!");
			return;
		}
		receiver.receive_packet(this, packet);

		if(this.sniffer != null) //Se este link tem um sniffer....
			this.sniffer.write_capture(packet);
	}
}
