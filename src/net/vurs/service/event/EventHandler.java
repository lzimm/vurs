package net.vurs.service.event;

public interface EventHandler<E extends Event> {

	public void handle(E event);
	
}
