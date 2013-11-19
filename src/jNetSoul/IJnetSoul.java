package jNetSoul;

public interface IJnetSoul
{
	public abstract void init();
	public abstract void authentication();
	public abstract void stateRequest();
	public abstract void ping();
	public abstract boolean isAlive();
	public abstract boolean isAuth();
	public abstract void close();
}