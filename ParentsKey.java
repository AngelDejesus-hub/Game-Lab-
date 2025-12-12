
public class ParentsKey extends Item {
	private static boolean ParentsKey;

	public ParentsKey() {
		super("Parentskey", ParentsKey);
		ParentsKey = false;	
	}

	@Override
	public void use() {
		if (Game.currentroom.getName().equals("Hallway")) {
			if (ParentsKey) {
				Game.print("You already opened your Parent's door!");
			} else {
				Game.currentroom.getExit(1).setdoor(false);
				ParentsKey = true;
				Game.print("Using the tool... you opened the door!");
			}
		} else {
			Game.print("You don't see any way to use the tool...");
	}
	}

}
