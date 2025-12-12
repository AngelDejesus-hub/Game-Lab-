
public class Chest extends Item{
	private static boolean open=false;

	public Chest() {
		super("Chest", true);
	}

	@Override
	public void open() {
		if (Game.currentroom.getName().equals("Secret Closet")) {
			if (open) {
	            Game.print("You already opened the chest.");
	        }else {
	        open = true;
	        Game.print("THIS WAS AN ALARM! YOUR PARENTS ARE HOME!");
	        Game.print("The game is restarting...");
	        Game.restartGame();
	        }
	        Game.print("You cannot open this here.");
	    }
	}
			
	}

