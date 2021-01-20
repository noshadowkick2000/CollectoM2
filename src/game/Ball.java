package game;

public class Ball {
	
	private final static COLOUR[] availableColours = COLOUR.values();
	
	public COLOUR colour;
	
	public Ball(int colour)
	{
		this.colour = availableColours[colour];
	}
	
	public Ball(COLOUR colour)
	{
		
	}
}
