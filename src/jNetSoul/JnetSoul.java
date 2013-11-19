package jNetSoul;

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

public class JnetSoul implements IJnetSoul {

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
	private static final String CLIENT = "jNetSoul";
	private static final String LOCATION = "JavaClass";

	private String _initAuth = "auth_ag ext_user none none";
	private String _authCommand;
	private Socket _jnetsoul;
	private PrintWriter _output;
	private BufferedReader _input;
	private String[] _serverInfo;
	private String[] _authArgs;
	private boolean _isAlive;
	private boolean _isAuth;

	public JnetSoul(String user, String pass)
	{
		String[] credentials;
		
		credentials = new String[2];
		credentials[LOGIN] = user;
		credentials[PASSWORD] = pass;
		try
		{
			this._jnetsoul = new Socket(NS_SERVER, NS_PORT);
			this._output = new PrintWriter(this._jnetsoul.getOutputStream(), true);
			this._input = new BufferedReader(new InputStreamReader(
					this._jnetsoul.getInputStream()));
			this._serverInfo = this._input.readLine().split(" ");
			this._authArgs = credentials;
			this._authCommand = this.generateAuthCommand();
			this._isAlive = true;
			this._isAuth = false;
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}
	}
	
	public JnetSoul(String[] args)
	{
		try
		{
			this._jnetsoul = new Socket(NS_SERVER, NS_PORT);
			this._output = new PrintWriter(this._jnetsoul.getOutputStream(), true);
			this._input = new BufferedReader(new InputStreamReader(
					this._jnetsoul.getInputStream()));
			this._serverInfo = this._input.readLine().split(" ");
			this._authArgs = args;
			this._authCommand = this.generateAuthCommand();
			this._isAlive = true;
			this._isAuth = false;
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see netsoul.IJnetsoul#init()
	 */
	@Override
	public void init()
	{
		this._output.println(this._initAuth);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see netsoul.IJnetsoul#authentication()
	 */
	@Override
	public void authentication()
	{
		this._output.println(this._authCommand);
	}

	public boolean isAuth()
	{
		try
		{
			String check = this._input.readLine();
			if (check.contains("rep 002"))
				return (this._isAuth = true);
		}
		catch (IOException e)
		{
			e.printStackTrace();
		}
		return (this._isAuth);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see netsoul.IJnetsoul#stateRequest()
	 */
	@Override
	public void stateRequest()
	{
		final String time = new Timestamp(new Date().getTime()).toString();
		final String hour = time.substring(11, time.length() - 4);
		this._output.println("state actif:" + hour);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see netsoul.IJnetsoul#ping()
	 */
	@Override
	public void ping()
	{
		try
		{
			String answer = this._input.readLine();
			if (answer.contains("ping"))
			{
				System.out.println(answer);
				this._output.println("ping backbitch");
				System.out.println("back");
			}
			else
			{
				System.out.println(answer);
			}
		} 
		catch (IOException e)
		{
			e.printStackTrace();
		}

	}

	public boolean isAlive()
	{
		return (this._isAlive);
	}

	public void close()
	{
		try
		{
			this._jnetsoul.close();
			this._input.close();
			this._output.close();
		}
		catch (IOException e)
		{
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
	private String generateAuthCommand()
	{
		try
		{
			final MessageDigest md = MessageDigest.getInstance("MD5");
			md.reset();
			md.update((_serverInfo[HASH_SEED] + "-" + _serverInfo[CLIENT_IP]
					+ "/" + _serverInfo[SERVER_PORT] + _authArgs[PASSWORD])
					.getBytes());

			final byte[] digest = md.digest();
			final BigInteger bigInt = new BigInteger(1, digest);
			final String mdstring = bigInt.toString(16);

			return (LOG + " " + _authArgs[LOGIN] + " " + mdstring + " " + CLIENT
					+ " " + LOCATION);
		}
		catch (NoSuchAlgorithmException e)
		{
			// Nothing to Do
			return (null);
		}
	}

}
