import javax.swing.JOptionPane;
import java.io.Serializable;

public class NPC implements Serializable {
	
	private String Name;
	
	public NPC(String N) {
		this.Name= N; 
	}
	public String getName() {
		return Name;

	}
	public void setName(String b) {
		Name=b;
	}
	public void say (String dialogue) {
		System.out.println(getName() + ": " + dialogue);
		
		
		
	}
	
	public void talk() {
		System.out.println("You can't talk to " + Name +"."); 
	}
	public int getResponse(String [] responses) {
		for (int i = 0; i < responses.length; i++) { //gives all the responses numbers 
	        System.out.println((i + 1) + ". " + responses[i]);
	    }
		
		String input = JOptionPane.showInputDialog(null, "Choose an option:");
		
		if (input == null) {
	        return -1; // returns default.
		}
	        try {
	            int choice = Integer.parseInt(input.trim()); //parse turns the string into ints. When pop-up box came up would not read correctly so this helps that. 
	            return choice;
	        } catch (NumberFormatException e) { //catches the error and returns to default aka -1 which is in the Jimmy class. 
	            return -1;
	        }
	    }
	public void give(Item item) {
		System.out.println(getName()+ " does not want the"+ item.getname()+ ".");
	}
	
	}


