import java.io.File;
import java.io.FileNotFoundException;
import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;
import java.util.ArrayList;

class Room implements Serializable {

	private String name;
	private Room[] roomarray;
	HashMap<String, Item> stuff;
	private boolean door;
	private HashMap<String, NPC> NPC;
	private boolean roomStatus;

	public Room(String n) {
		stuff = new HashMap<>();
		NPC = new HashMap<>();
		name = n;
		roomarray = new Room[6];
		World.saveroom.add(this);

	}

	public String getName() {
		return name;
	}

	public boolean getdoor() {
		return door;
	}

	public void setdoor(boolean door) {
		this.door = door;
	}

	public void addExit(Room r, int i) {
		roomarray[i] = r;
	}

	public Room getExit(int i) {
		return roomarray[i];
	}

	public String toString() {
		
		if (Game.RoomDesc.containsKey(name) && roomStatus()) {
			//ArrayList<String> descList = Game.RoomDesc.get(name);
			String desc = "";

		    // Does this room have descriptions in the map?
		    if (Game.RoomDesc.containsKey(name)) {
		        ArrayList<String> descList = Game.RoomDesc.get(name);

		        if (roomStatus() && descList.size() > 1) {
		            desc = descList.get(1); //If status is true dirty, uses the second desc
		        } else {
		            desc = descList.get(0); //clean uses firs
		        }
		    }
		}
		    
		
		String exist = name + "\n" + Game.RoomDesc.get(name) + "\nItems here: ";
		if (stuff.isEmpty()) {
			exist += "none";
		} else {
			for (String key : stuff.keySet()) {
				exist += key +","+ " ";
			}
		}
			
			exist += "\nNPC's here: ";
			if (NPC.isEmpty()) {
				exist += "none\n";
			} else {
				 for (String npc : NPC.keySet()) {
			            exist += npc + " ";
				 }
			}
			return exist;
	}

	public void addItem(Item thing) {
		stuff.put(thing.getname(), thing);
	}

	public Item getItems(Item item) {
		return stuff.get(item);
	}

	public Item removeItems(String thing) {
		return stuff.remove(thing);
	}

	public HashMap<String, Item> getstuff() {
		return stuff;
	}

	public void addNPC(NPC npc) {
		NPC.put(npc.getName().toLowerCase(), npc);
	}

	public NPC getNPC(String name) {
		if (name == null) {
			return null;
		}
		return NPC.get(name.toLowerCase());
	}

	public boolean hasAnyNPC() {
		return !NPC.isEmpty();
	}

	public boolean roomStatus() {
		return roomStatus;
	}

	public void setStatus(boolean roomStatus) {
		this.roomStatus = roomStatus;
	}

}
