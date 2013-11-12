package netsoul;

import java.util.Date;

public class Main {

	public static void main(String[] args) {

		IJnetsoul jns = new Jnetsoul(args);
		args = null;
		jns.init();
		jns.authentication();
		if (jns.isAuth()) {
			jns.stateRequest();
			while (jns.isAlive()) {
				jns.ping();
				System.out.println(new Date().toString());
			}
		}
		jns.close();
	}
}
