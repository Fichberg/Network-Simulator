public class Host
{
	int id;               //identificador do host. Este identificador é único para este host
	String computer_ip;   //endereço IP do computador
    String router_ip;     //endereço ip do roteador padrao
    String dns_server_ip; //endereço ip do servidor dns


	//Construtor
	public Host(int id)
	{
		this.id = id;
		this.computer_ip = null;
		this.router_ip = null;
		this.dns_server_ip = null;
	}

	//setters
	void set_computer_ip(String computer_ip)
	{
		if(this.computer_ip == null)
			this.computer_ip = computer_ip;
	}

	void set_router_ip(String router_ip)
	{
		if(this.router_ip == null)
			this.router_ip = router_ip;
	}

	void set_dns_server_ip(String dns_server_ip)
	{
		if(this.dns_server_ip == null)
			this.dns_server_ip = dns_server_ip;
	}

	//getters
	public int get_id()
	{
		return this.id;
	}
}