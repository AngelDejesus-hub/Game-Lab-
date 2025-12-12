
public class BathroomKey extends Item {
	private static boolean BathroomKey;

	public BathroomKey() {
		super("BathroomKey", BathroomKey);
		BathroomKey = false;	
	}

	@Override
	public void use() {
		if (Game.currentroom.getName().equals("Hallway")) {
			if (BathroomKey) {
				Game.print("You already opened the Bathroom door!");
			} else {
				Game.currentroom.getExit(0).setdoor(false);
				BathroomKey = true;
				Game.print("Using the tool... you opened the door!");
			}
		} else
			Game.print("You don't see any way to use the tool...");
	}

}
