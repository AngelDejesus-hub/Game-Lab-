import java.io.Serializable;

public class Item implements Serializable {
	private String name;
	private boolean isheavy;

	public Item(String n, boolean h) {
		name = n;
		isheavy = h;
	}

	public String getname() {
		return name;
	}

	public void setName(String a) {
		name = a;
	}

	public String toString() {
		return name;
	}

	public boolean getheavy() {
		return isheavy;
	}

	public void use() {
		Game.print("You can not use this item"); 
		
	}
	public void open() {
		Game.print("You can not open this");
	}
	public void close() {
		Game.print("You can not close this");
	}
	public void read() {
		Game.print("You can not read this");
	}
	public void destroy() {
		Game.print("You can not destroy this");
	}


}
