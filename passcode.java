
public class passcode extends Item {
	private static boolean passcode;
	
	public passcode() {
		super("4914", passcode);
		passcode=false; 
		
	}
		@Override
		public void use() {
			if (Game.currentroom.getName().equals("Parent's Room")) {
				if (passcode) {
					Game.print("You already opened the walk-in Closet!");
				} else {
					passcode = true;
					Game.print("you type in the code 4194 into the lock\n");
					Game.print("You find YOUR XBOX\n");
					Game.print("Better go use it in your room before your parents come back home\n");
					Item Xbox= new Xbox();
					Game.addinventory(Xbox);
					
				}
			} else
				Game.print("You don't see any way to use the tool...");
		}
		
	}


