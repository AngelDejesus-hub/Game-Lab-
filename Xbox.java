
public class Xbox extends Item {
	private static boolean Xbox;

	public Xbox() {
		super("Xbox", Xbox);
		Xbox = false;	
	}

	@Override
	public void use() {
		if (Game.currentroom.getName().equals("BedRoom")) {
			if (Xbox) {
				Game.print("You already used it.");
			} else {
				Xbox = true;
				Game.print("You finally find your xbox and get to play but");
				Game.print("REALIZE YOUR MOM HID THE CONTROLLER FOR THE XBOX!!!");
				Game.print("Another day, you will have to findmy controller");
				Game.restartGame();
			}
		} else
			Game.print("You don't see any way to use the XBOX...");
	}

}
