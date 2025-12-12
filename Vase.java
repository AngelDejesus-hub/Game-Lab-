
public class Vase extends Item {
	private static boolean broken = false;

	public Vase() {
		super("vase", true);
	}

	@Override
	public void destroy() {
		if (Game.currentroom.getName().equals("Hallway")) {
			if (broken) {
				Game.print("You already broke this.");
			} else {
				broken = true;
				Game.print("You broke the vase and found the key to the attic");
				Item atticKey = new AtticKey();
				Game.addinventory(atticKey);
			}
		}
	}
}
