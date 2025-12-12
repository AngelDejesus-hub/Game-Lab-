
public class AtticKey extends Item {
	private static boolean AtticKey;

	public AtticKey() {
		super("AtticKey", AtticKey);
		AtticKey = false;
	}

	@Override
	public void use() {
		if (Game.currentroom.getName().equals("Hallway")) {
			if (AtticKey) {
				Game.print("You already opened the Attic door!");
			} else {
				Game.currentroom.getExit(4).setdoor(false);
				AtticKey = true;
				Game.print("Using the tool... you opened the door!");
			}
		} else
			Game.print("You don't see any way to use the tool...");
	}

}
