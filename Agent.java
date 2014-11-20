/*Esta classe abstrata representa um agente da rede (conf. AgentEnum)*/
public abstract class Agent {
	
	public abstract String get_name();
	public abstract String get_type();
	public abstract void set_residence(Object obj);
	public abstract void receive_command(String command);
	
}
