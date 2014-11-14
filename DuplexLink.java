import java.io.*;

public class DuplexLink
{
	/* point_A e point_B guardam quem são os componentes da rede dos extremos
	   deste duplex-link. Os extremos podem ser tanto hosts quanto routers, e
	   são armazenadas com o hX, ou rY.P {P é a porta e X e Y são os
	   identificadores do componente}.*/
	private String point_A;
	private String point_B;
	private float mbps_capacity; // capacidade deste duplex-link, em MBps.
	private float latency;       //a latência [aka atraso] deste duplex-link em ms.

	//Construtor
	public DuplexLink(String point_A, String point_B, float mbps_capacity, float latency)
	{
		this.point_A = point_A;
		this.point_B = point_B;
		this.mbps_capacity = mbps_capacity;
		this.latency = latency;
	}

	/*"setters" aqui são desnecessários, pois a configuração é ditada pelo arquivo
	 de entrada e é imutável. */

	//getters
	public String get_point_A()
	{
		return this.point_A;
	}

	public String get_point_B()
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
	//vê se determinado ponto e de host
	public boolean check_if_host_point(String point)
	{
		return point.charAt(0) == 'h';
	}

	//ve se determinado ponto e de router
	public boolean check_if_router_point(String point)
	{
		return point.charAt(0) == 'r';
	}

	//retorna o identificador do host do ponto. Retorna -1 se o argumento não for um host
	public int get_host_point_id(String host_point)
	{
		if(check_if_host_point(host_point))
			return Integer.parseInt(host_point.substring(1));
		return -1;
	}

	//retorna o identificador do router do ponto. Retorna -1 se o argumento não for um router
	public int get_router_point_id(String router_point)
	{
		if(check_if_router_point(router_point))
			return Integer.parseInt(router_point.substring(1, router_point.indexOf('.')));
		return -1;
	}

	//retorna a porta do router do ponto. Retorna -1 se o argumento não for um router
	public int get_router_port(String router_point)
	{
		if(check_if_router_point(router_point))
			return Integer.parseInt(router_point.substring(router_point.indexOf('.') + 1));
		return -1;
	}
}
