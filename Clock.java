import java.io.*;

public class Clock
{
	private long initial_time;    //Guarda o tempo inicial do clock

	public Clock()
	{
		this.initial_time = System.nanoTime();
	}

	//Calcula o tempo de execucao em nanosegundos e o retorna
	public long execution_time_in_nanos()
	{
		long time_now = System.nanoTime();
		return time_now - this.initial_time;
	}

	//Calcula o tempo de execucao em milisegundos e o retorna
	public long execution_time_in_milis()
	{
		long time_now = System.nanoTime();
		return (time_now - this.initial_time) / 1000000;
	}

	//Calcula o tempo de execucao em segundos e o retorna
	public long execution_time_in_seconds()
	{
		long time_now = System.nanoTime();
		return (time_now - this.initial_time) / 1000000000;
	}

	//Retorna a string com o tempo de execucao na forma XhYmZs
	public String hms_format(long time_in_seconds)
	{
		long h = 0, m = 0, s;
		String time_string = "";
		if(time_in_seconds / 3600 >= 1)
		{
			h = time_in_seconds / 3600;
			time_in_seconds = time_in_seconds % 3600;	
		}
		if(time_in_seconds / 60 >= 1)
		{
			m = time_in_seconds / 60;
			time_in_seconds = time_in_seconds % 60;
		}
		s = time_in_seconds;
		return time_string + h + "h" + m + "m" + s + "s";
	}
}