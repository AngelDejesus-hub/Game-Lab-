import java.util.Scanner;
import java.util.ArrayList;
import java.util.HashMap;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.io.File;
import java.io.FileInputStream;

public class Game {

	public static void main(String[] args) {
		gui = new GameSwingGUI();
		gui.setVisible(true);
		print("\nWelcome to findmy xbox");
		currentroom = World.buildWorld();
		loaddescription();
		print(currentroom);
	}

	public static GameSwingGUI gui;
	public static ArrayList<Item> inventory = new ArrayList<>();
	public static HashMap<String, ArrayList<String>> RoomDesc = new HashMap<>();

	public static void runGame(String command) {

		String[] parts = command.split(" ", 2);
		String action = parts[0];
		String argument = null;

		if (parts.length > 1)
			argument = parts[1];
		if (action.equals("east")) {
			move(action);
		}
		if (action.equals("west")) {
			move(action);
		}
		if (action.equals("north")) {
			move(action);
		}
		if (action.equals("south")) {
			move(action);
		}
		if (action.equals("up"))
			move(action);
		if (action.equals("take")) {
			takeItem(argument);
		}
		if (action.equals("i")) {
			checki();
		}
		if (action.equals("save")) {
			saveFile();
		}
		if (action.equals("load")) {
			loadFile();
		}
		if (action.equals("use")) {
			use(argument);
		}
		if (action.equals("open")) {
			open(argument);
		}
		if (action.equals("talk")) {
			talk(argument);
		}
		if (action.equals("give")) {
			give(argument);
		}
		if (action.equals("clean")) {
			clean();
		}
		if (action.equals("destroy")) {
			destroy(argument);
		}
		if (action.equals("read")) {
			read(argument); 
		}
	}
	public static void restartGame() {
	    Game.print("Restarting game...");
	    main(null);  
	}

	private static void destroy(String argument) {

		if (argument == null) {
			Game.print("What would you like to destroy: ");
			return;
		}
		Item i = currentroom.removeItems(argument);
		if (i == null)
			Game.print("There is no item to be destroyed");
		i.destroy();
		Game.print("\nYou destroyed the "+ argument);
	}

	private static void use(String argument) {
		if (argument== null) {
			Game.print("use what?");
			return;
		}
		for (Item i : inventory) {
			if (i.getname().equalsIgnoreCase(argument)) {
				i.use();
				return;
			}
		}
		System.out.println("You can't use this item because it is not in your inventory");

	}

	public static void addinventory(Item i) {
		inventory.add(i);
	}

	public static void removeinventory(Item i) {
		inventory.remove(i);
	}

	private static void open(String argument) {
		if (argument== null) {
			Game.print("Open what?");
			return;
		}
		for (Item i : currentroom.getstuff().values()) {
			if (i.getname().equalsIgnoreCase(argument)) {
				i.open();
				
				return;
			}
		}
	}
	private static void read(String argument) {
		if (argument== null) {
			Game.print("Read what?");
			return;
		}
		for (Item i : inventory) {
			if (i.getname().equalsIgnoreCase(argument)) {
				i.read();
				return;
			}
		}
	}

	public static void clean() {
		if (Game.currentroom.roomStatus() == false) {
			Game.print("This room is already clean");
		} else {
			currentroom.setStatus(true);
			Game.print("You have cleaned the " + currentroom.getName());
			if (currentroom.getName().equals("Bathroom")) {
				Item pkey = new ParentsKey();
				currentroom.stuff.put("ParentsKey", pkey);
				Game.print("\nGreat job you have cleaned the bathroom");
				Game.print(currentroom);
			}

		}

	}

	public static void talk(String npcName) {
		if (npcName == null) {
			Game.print("Who do you want to talk to");
			return;
		}
		npcName = npcName.trim(); // Trim removes any spaces.

		if (npcName.startsWith("to ")) {
			npcName = npcName.substring(3).trim();
		}

		NPC npc = currentroom.getNPC(npcName);
		if (npc == null) {
			Game.print(npcName + " is not here");
		} else {
			npc.talk();
		}

	}

	public static void give(String argument) {
		if (argument == null) { // from talk method
			Game.print("Give what to who?");
			return;
		}

		// lastIndexof takes the last "to" if there was ever another to
		// substring is a portion of the original String.
		int toIndex = argument.lastIndexOf(" to "); // when existing it is give toIndex which is also a 4 large
		if (toIndex == -1) { // if "to" does not exist it is set to -1
			Game.print("Please use: give <item> to <npcName>"); // <baodjak>
			return;
		}

		String npcName = argument.substring(toIndex + 4).trim(); // from start to finish after "to" the name should be
																	// there
		String itemName = argument.substring(0, toIndex).trim(); // After Give should always be the name of the Item

		if (npcName.startsWith("to ")) { // 3 spaces long
			npcName = npcName.substring(3).trim(); // starting from the space after would be the name. Adding trim
													// removes the space and ends up with the name.
		}

		NPC npc = currentroom.getNPC(npcName); // Checks if NPC is in the room
		if (npc == null) {
			Game.print(npcName + " is not here");
			return;
		}
		Item itemGive = null; // Give the item null so we can check through the inventory.
		for (Item i : inventory) {
			if (i.getname().equalsIgnoreCase(itemName)) {
				itemGive = i; // giving i means it exists in the inventory.
			}
		}
		if (itemGive == null) { // stays null means you do not have it.
			Game.print("You do not have" + itemName);
		}
		npc.give(itemGive); // uses give method.
	}

	public static void print(Object obj) {
		if (gui != null) {
			gui.append(obj.toString());
		} else {
			System.out.print(obj.toString());
		}

	}

	public static Room currentroom;

	public static void move(String c) {
		c = c.toLowerCase();
		Room nextRoom = null;
		switch (c) {
		case "east":
			nextRoom = currentroom.getExit(0);
			print("You are going East\n");
			break;
		case "west":
			nextRoom = currentroom.getExit(1);
			print("You are going West\n");
			break;
		case "north":
			nextRoom = currentroom.getExit(2);
			print("You are going North\n");
			break;
		case "south":
			nextRoom = currentroom.getExit(3);
			print("You are going South\n");
			break;
		case "up":
			nextRoom = currentroom.getExit(4);
			print("You are going Up\n");
			break;

		}
		if (nextRoom == null) {
			Game.print("You can't go that way ");
			return;
		} else if (nextRoom.getdoor()) {
			Game.print("\nDoor is locked. Go find the key ");
		} else
			currentroom = nextRoom;
		print(currentroom);

	}

	public static void checki() {
		if (inventory.isEmpty()) {
			Game.print("You are carrying nothing! ");
		} else {
			for (Item i : inventory) {
				Game.print("You are carrying: ");
				Game.print(i);
			}
		}
	}

	public static void takeItem(String itemName) {
		if (itemName == null) {
			Game.print("What would you like to take: ");
			return;
		}
		Item i = currentroom.removeItems(itemName);
		if (i == null)
			Game.print("ITEM WAS NOT FOUND!!");

		else if (i.getheavy()) {
			Game.print("Item is too heavy please pick another item");

		} else {
			Game.print("You have a " + itemName);
			inventory.add(i);
		}
	}

	public static void saveFile() {

		File saveFile = new File("save0.sav"); // name the file yourself
		try {
			saveFile.createNewFile();
			ObjectOutputStream outStream = new ObjectOutputStream(new FileOutputStream(saveFile));
			outStream.writeObject(currentroom);
			outStream.writeObject(inventory);
			outStream.writeObject(World.saveroom);
			outStream.close();
			Game.print("Game saved!");
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public static void loadFile() {
		File saveFile = new File("save0.sav"); // name of save file
		try {
			saveFile.createNewFile();
			ObjectInputStream inStream = new ObjectInputStream(new FileInputStream(saveFile));
			currentroom = (Room) inStream.readObject();
			inventory = (ArrayList<Item>) inStream.readObject();
			World.saveroom = (ArrayList<Room>) inStream.readObject();
			inStream.close();
			Game.print("Loaded Save File!\n");
			Game.print(currentroom);
		} catch (IOException e) {
			e.printStackTrace();
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		}

	}

	public static void loaddescription() {
		try {
			File textFile = new File("Roomdesc.txt");
			Scanner scan = new Scanner(textFile);

			while (scan.hasNextLine()) {
				// Read room
				String roomName = scan.nextLine();
				if (roomName.equals("")) { // if it equals nothing it continues the loop
					continue;
				}
				ArrayList<String> descList = new ArrayList<>();
				String description = "";
				String line = scan.nextLine();

				while (!line.equals("#")) {
					if (line.equals("~")) { // if ~
						if (!description.equals("")) { // and if description is not nothing
							descList.add(description); // add the description in the arraylist.
						}
						description = "";
						if (scan.hasNextLine()) {
							roomName = scan.nextLine();
						}

					} else {
						description += line + "\n";
					}
					if (!scan.hasNextLine()) {
						break;
					}
					line = scan.nextLine();
				}
				if (!description.trim().equals("")) {
					descList.add(description.trim());
				}
				// store the room name and its description
				RoomDesc.put(roomName, descList);
			}

			scan.close();
		} catch (FileNotFoundException ex) {
			System.out.println("The file isn't there");
		}
	}

}