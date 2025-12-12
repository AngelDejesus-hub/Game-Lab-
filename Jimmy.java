
public class Jimmy extends NPC {

	private int convoNumber = 0;

	public Jimmy(String N) {
		super("jimmy");
	}

	@Override
	public void talk() {

		int option;

		switch (convoNumber) {

		case 0: {
			say("What's up bro, How you been");
			String[] responses = { 
					"Good, Do you know where the keys to mom's room is?",
					"Terrible, where is Mom and Dad?", 
					"Just chillin, How you been?" };

			option = getResponse(responses);

			switch (option) {
			case 1:
				say("Uhhhhhh I could know.");
				convoNumber = 1;
				break;

			case 2:
				say("I am not sure do you want me to call them or something?");
				convoNumber = 2;
				break;

			case 3:
				say("I'm doing good just chillin on the couch");
				convoNumber = 3;
				break;

			default:
				say("Bro what?");
				break;
			}
			break;
		}
		// Case 1 cont.
		case 1: {
			String[] responses1 = {

					"I'm trying to find my Xbox", 
					"Wait nevermind I already have them." };

			option = getResponse(responses1);

			if (option == 1) {
				say("Go find me a cookie and I will give you the key to the bathroom.");
			} else if (option == 2) {
				say("Hurry up before they gets back!");
			} 

			convoNumber = 4;
			break;
		}
		
		// conv2 cont.
		
		case 2: {
			String[] responses2 = { 
					"No don't call them, just take a guess", 
					"Yea call them!",
					"No nevermind it's whatever" };

			option = getResponse(responses2);

			if (option == 1) {
				say("uhhhhh idk bro probably out at restaurant. They could be back in a hour.");
			} else if (option == 2) {
				say("They said they're eating at Chili's right now. They'll be back in 30 minutues.");
			} else {
				say("Oh okay");
			}

			convoNumber = 4;
			break;
		}
		
		//conv3 cont. 

		case 3: {
			String[] responses3 = { 
					"yea anyways I'm trying to find my Xbox, any tips?", 
					"Can I chill with you?" };
			option = getResponse(responses3);

			if (option == 1) {
				say("Your Xbox? I'd check the mom's room if you can get in there.");
			} else if (option == 2) {
				say("Sure, pull up a chair.");
			}

			convoNumber = 4;
			break;
		}
		
		//Ending conv. 
		default: {
			say("Well, I'm going to go back to doing nothing.");
			convoNumber=0;
			break;
		}
		}
	}
	
	@Override
	
	public void give(Item item) {
		if (item.getname().equalsIgnoreCase("cookie")) { //IgnoreCase: self explanatory took from internet because having it all be lowercase is annoying 
			say("Hell yea, thanks bro!");
			Game.removeinventory(item);
			Item bathKey= new BathroomKey(); 
			Game.addinventory(bathKey);
			// Giving a Bathroomkey a class so it can used to open the bathroom door later. 
			
		}
			
		
		
	}
}
