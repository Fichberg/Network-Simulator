import java.io.*;

public class DuplexLink
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
		return this.latency;
	}

	public float get_latency()
	{
		return this.latency;
	}

	//outras funções relacionadas a manipulação de objetos DuplexLink
	
	//verifica se o link contém as duas pontas
	public boolean has_edges(String point_A, String point_B)
	{
		String edge_one = this.point_A.get_name();
		String edge_two = this.point_B.get_name();
		if ( (edge_one.equals(point_A) || edge_one.equals(point_B)) &&
			 (edge_two.equals(point_A) || edge_two.equals(point_B)))
			return true;
		return false;
	}
	
	//vê se determinado ponto é de host
	public boolean is_host_point(String point)
	{
		Node ponto = null;
		if (this.point_A.get_name().equals(point))
			ponto = this.point_A;
		else if(this.point_B.get_name().equals(point))
			ponto = this.point_B;
		else
			System.err.println("ponto inexistente");
		if (ponto instanceof Host)
			return true;
		return false;
	}

	//ve se determinado ponto é de router
	public boolean is_router_point(String point)
	{
		Node ponto = null;
		if (this.point_A.get_name().equals(point))
			ponto = this.point_A;
		else if(this.point_B.get_name().equals(point))
			ponto = this.point_B;
		else
			System.err.println("ponto inexistente");
		if (ponto instanceof Router)
			return true;
		return false;
	}

}
