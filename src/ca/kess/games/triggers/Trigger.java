package ca.kess.games.triggers;


public interface Trigger {

	public String getName();
	
	public void addChild(Triggerable child);
	
	public void reset();
	
	public void activate();
	
	public void deactivate();
	
	public void update(float dt);
}
