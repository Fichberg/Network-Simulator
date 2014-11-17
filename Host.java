public class Host extends Node
{
	private String name;          //identificador do host. Este identificador é único.
	private String computer_ip;   //endereço IP do computador
    private String router_ip;     //endereço ip do roteador padrao
    private String dns_server_ip; //endereço ip do servidor dns
    private Agent agent;

	//Construtor
	public Host(String name)
	{
		this.name = name;
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
	void set_agent(Agent agent)
	{
		this.agent = agent;
	}

	//getters
	public String get_name()
	{
		return this.name;
	}
}
