package netsoul;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.math.BigInteger;
import java.net.Socket;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.sql.Timestamp;
import java.util.Date;

public class Jnetsoul implements IJnetsoul {

	/* Server Constants */
	private static final String NS_SERVER = "ns-server.epita.fr";
	private static final int NS_PORT = 4242;

	/* Server Info position in array Constants */
	private static final int HASH_SEED = 2;
	private static final int CLIENT_IP = 3;
	private static final int SERVER_PORT = 4;

	/* Args Constants */
	private static final int LOGIN = 0;
	private static final int PASSWORD = 1;

	/* Auth command Constants */
	private static final String LOG = "ext_user_log";
	private static final String CLIENT = "Jnetsoul";
	private static final String LOCATION = "Phone";

	private String initAuth = "auth_ag ext_user none none";
	private String authCommand;
	private Socket jnetsoul;
	private PrintWriter output;
	private BufferedReader input;
	private String[] serverInfo;
	private String[] authArgs;
	private boolean isAlive;
	private boolean isAuth;

	public Jnetsoul(String[] args) {
		try {
			this.jnetsoul = new Socket(NS_SERVER, NS_PORT);
			this.output = new PrintWriter(this.jnetsoul.getOutputStream(), true);
			this.input = new BufferedReader(new InputStreamReader(
					this.jnetsoul.getInputStream()));
			this.serverInfo = this.input.readLine().split(" ");
			this.authArgs = args;
			this.authCommand = this.generateAuthCommand();
			this.isAlive = true;
			this.isAuth = false;
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see netsoul.IJnetsoul#init()
	 */
	@Override
	public void init() {
		this.output.println(this.initAuth);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see netsoul.IJnetsoul#authentication()
	 */
	@Override
	public void authentication() {
		this.output.println(this.authCommand);
	}

	public boolean isAuth() {
		try {
			String check = this.input.readLine();
			if (check.contains("rep 002"))
				return (this.isAuth = true);
		} catch (IOException e) {
			e.printStackTrace();
		}
		return (isAuth);

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see netsoul.IJnetsoul#stateRequest()
	 */
	@Override
	public void stateRequest() {
		final String time = new Timestamp(new Date().getTime()).toString();
		final String hour = time.substring(11, time.length() - 4);
		this.output.println("state actif:" + hour);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see netsoul.IJnetsoul#ping()
	 */
	@Override
	public void ping() {
		try {
			String answer = this.input.readLine();
			if (answer.contains("ping")) {
				System.out.println(answer);
				this.output.println("ping backbitch");
				System.out.println("back");
			} else {
				System.out.println(answer);
			}
		} catch (IOException e) {
			e.printStackTrace();
		}

	}

	public boolean isAlive() {
		return (this.isAlive);
	}

	public void close() {
		try {
			this.jnetsoul.close();
			this.input.close();
			this.output.close();
		} catch (IOException e) {
			e.printStackTrace();
		}

	}

	/**
	 * create and returns the authentication commands
	 * 
	 * @param serverInfo
	 * @param authArgs
	 * @return authCommand
	 */
	private String generateAuthCommand() {
		try {
			final MessageDigest md = MessageDigest.getInstance("MD5");
			md.reset();
			md.update((serverInfo[HASH_SEED] + "-" + serverInfo[CLIENT_IP]
					+ "/" + serverInfo[SERVER_PORT] + authArgs[PASSWORD])
					.getBytes());

			final byte[] digest = md.digest();
			final BigInteger bigInt = new BigInteger(1, digest);
			final String mdstring = bigInt.toString(16);

			return LOG + " " + authArgs[LOGIN] + " " + mdstring + " " + CLIENT
					+ " " + LOCATION;
		} catch (NoSuchAlgorithmException e) {
			// Nothing to Do
			return null;
		}
	}

}
