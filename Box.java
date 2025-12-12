
public class Box extends Item {
	private static boolean isOpen=false;
	private static boolean broken=false;

	public Box() {
		super("box", true);
	}

	@Override
	public void use() {
		Game.print("Either open or close the box.");
	}

	@Override
	public void open() {
		if (isOpen)
			Game.print("The box is already open idiot...");
		else {
			isOpen = true;
			Game.print(" You open the box and find... NOTHING");
			Game.print("\nJimmy: *laughs*");
			Game.print("\nYou should try destroying it, its basically garbage at that point.");
		}
	}

	public void close() {
		if (isOpen) {
			Game.print("You close the box with disapointment");
			isOpen = false;
		} else {
			Game.print("You already closed the box");

		}
	}
	@Override
	public void destroy() {
		if (Game.currentroom.getName().equals("Living Room")) {
			if (broken) {
				Game.print("You already broke this.");
			} else {
				broken = true;
				Game.print("You broke the box and found the key to the kitchen");
				Item Kitchenkey = new KitchenKey();
				Game.addinventory(Kitchenkey);
			}
		}
	}
}
