import java.util.Date;
import jNetSoul.*;

public class Program
{
	public static void main(String[] args)
	{
		IJnetSoul jns = new JnetSoul(args);
		
		jns.init();
		jns.authentication();
		if (jns.isAuth())
		{
			jns.stateRequest();
			while (jns.isAlive())
			{
				jns.ping();
				System.out.println(new Date().toString());
			}
		}
		jns.close();
	}
}