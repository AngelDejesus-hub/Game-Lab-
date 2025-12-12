
public class HallwayKey extends Item {
	private static boolean HallwayKey;

	public HallwayKey() {
		super("hallwaykey", HallwayKey);
		HallwayKey = false;
	}

	@Override
	public void use() {
		if (Game.currentroom.getName().equals("BedRoom")) {
			if (HallwayKey) {
				Game.print("You already opened the Hallway door!");
			} else {
				Game.currentroom.getExit(3).setdoor(false);
				HallwayKey = true;
				Game.print("Using the tool... you opened the door!");
			}
		} else
			Game.print("You don't see any way to use the tool...");
	}

}
