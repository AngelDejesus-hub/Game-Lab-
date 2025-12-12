import java.io.File;
import java.util.Scanner;

public class NoteBook extends Item {
	public NoteBook() {
		super("NoteBook", false);
	}
	@Override
	public void use() {
		Game.print("Maybe try reading it instead...");
	}

	@Override
	public void read() {
		Game.print("You read it, it gives you a code for the cabinet in your parent's room");
		Game.currentroom.addItem(new passcode());
		Game.addinventory(new passcode());
		Game.removeinventory(this);
	}
}