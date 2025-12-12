import java.util.ArrayList;

public class World {
	// storing rooms
	public static ArrayList<Room> saveroom = new ArrayList<>();

	public static Room buildWorld() {
		
		// Created Rooms
		Room bedRoom = new Room("BedRoom");
		Room hallWay = new Room("Hallway");
		Room BathRoom = new Room("Bathroom");
		Room ParentsRoom = new Room("Parent's Room");
		Room LivingRoom = new Room("Living Room");
		Room Kitchen = new Room("Kitchen");
		Room SecretCloset = new Room("Secret Closet");
		Room walkinCloset= new Room("Walk-in Closet");
		
		//locked rooms
		hallWay.setdoor(true);
		SecretCloset.setdoor(true);
		BathRoom.setdoor(true); 
		Kitchen.setdoor(true);
		ParentsRoom.setdoor(true);
		walkinCloset.setdoor(true);
		
		//creating the NPCS
		NPC jimmy = new Jimmy("Jimmy"); 

		// Exits
		bedRoom.addExit(hallWay, 3); // 3 south
		hallWay.addExit(bedRoom,2); // 2 north
		hallWay.addExit(SecretCloset, 4); // 4 up
		SecretCloset.addExit(hallWay, 1); // 1 west
		hallWay.addExit(BathRoom, 0); // 0 east
		BathRoom.addExit(hallWay, 1); // 1 west
		hallWay.addExit(ParentsRoom, 1); // 1 west
		ParentsRoom.addExit(hallWay, 0); // 0 east
		hallWay.addExit(LivingRoom, 3); // 3 south
		LivingRoom.addExit(Kitchen, 1); // 1 west
		LivingRoom.addExit(hallWay, 2); // 2 north
		Kitchen.addExit(LivingRoom, 0); // 0 East
		ParentsRoom.addExit(walkinCloset, 2); // 2 north 
		

		// item
		bedRoom.addItem(new HallwayKey()); // able to hold
		bedRoom.addItem(new Item("Bed", true)); // Too heavy to hold
		Kitchen.addItem(new Item("cookie",false)); //able to hold
		hallWay.addItem(new Vase()); //able to hold 
		SecretCloset.addItem(new Chest()); // Too heavy to hold
		ParentsRoom.addItem(new NoteBook());
		LivingRoom.addItem(new Box());
		
		
		//Locked Item 
		
		
		//Adding the NPCs into specific room  
		LivingRoom.addNPC(jimmy);
		
		//Status for rooms
		BathRoom.setStatus(true);
		
		return bedRoom;
	}

}
