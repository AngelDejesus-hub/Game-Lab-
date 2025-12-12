
public class KitchenKey extends Item {
	private static boolean KitchenKey;

	public KitchenKey() {
		super("KitchenKey", KitchenKey);
		KitchenKey = false;
	}

	@Override
	public void use() {
		if (Game.currentroom.getName().equals("Living Room")) {
			if (KitchenKey) {
				Game.print("You already opened the Kicthen door!");
			} else {
				Game.currentroom.getExit(1).setdoor(false);
				KitchenKey = true;
				Game.print("Using the tool... you opened the door!");
			}
		} else
			Game.print("You don't see any way to use the tool...");
	}

}
