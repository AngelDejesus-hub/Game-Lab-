import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.*;
import java.io.File;
import java.io.OutputStream;
import java.io.PrintStream;
import java.lang.reflect.*;
import java.util.*;

/**
 * Swing GUI for your text adventure.
 *
 * Key points: - Works with your existing Game class (no edits to Game.java). -
 * Save/Load: calls Game.saveFile() / Game.loadFile() if present (no args).
 * Falls back to save(File|String) / load(File|String) if those exist. - No
 * dialogs; “save” and “load” just invoke your methods. - After Load: prints
 * current room and refreshes inventory panel.
 */
public class GameSwingGUI extends JFrame {
	// --- Transcript & input ---
	private final JTextArea transcript = new JTextArea();
	private final JTextField commandField = new JTextField();
	private final JButton btnDo = new JButton("DO");

	// --- D-Pad ---
	private final JButton btnN = new JButton("▲");
	private final JButton btnS = new JButton("▼");
	private final JButton btnW = new JButton("◀");
	private final JButton btnE = new JButton("▶");
	private final JButton btnU = new JButton("Up");

	// --- Status & inventory ---
	private final JLabel status = new JLabel(
			"Use arrows or type: North, South, East, West, Up | take <item> | i | save | load");
	private final DefaultListModel<String> inventoryModel = new DefaultListModel<>();
	private final JList<String> inventoryList = new JList<>(inventoryModel);

	// --- Reflection handles (some optional) ---
	private Class<?> gameClass, worldClass;
	private Method gameMove, gamePrint, gameTakeItem, gameCheckI, gameLoadDesc, worldBuildWorld;
	private Field gameCurrentRoom, gameInventory;

	// Save/Load (detected dynamically)
	private Method gameSave_File, gameSave_String, gameLoad_File, gameLoad_String;
	// Your no-arg methods:
	private Method gameSave_NoArg, gameLoad_NoArg;

	// --- GUI-side fallback inventory if Game.inventory not present ---
	private final java.util.List<String> guiInventoryFallback = new ArrayList<>();

	public GameSwingGUI() {
		super("Find My Xbox — GUI");
		setDefaultCloseOperation(EXIT_ON_CLOSE);
		setSize(1040, 680);
		setLocationRelativeTo(null);

		setJMenuBar(buildMenuBar());
		JToolBar tb = buildToolbar();

		JPanel content = new JPanel(new BorderLayout());
		content.add(tb, BorderLayout.NORTH);
		setContentPane(content);

		buildUI(content);
		hookSystemOut();
		initReflection();
		initGameState();
		printCurrentRoomOnce();
		refreshInventoryBox();

		// Optional: autosave on close using your Game.saveFile() if available
		addWindowListener(new WindowAdapter() {
			@Override
			public void windowClosing(WindowEvent e) {
				doSaveNoPrompt();
			}
		});
	}

	// ---------------- UI ----------------
	private JMenuBar buildMenuBar() {
		JMenuBar mb = new JMenuBar();
		JMenu file = new JMenu("File");
		JMenuItem miSave = new JMenuItem("Save");
		JMenuItem miLoad = new JMenuItem("Load");
		JMenuItem miExit = new JMenuItem("Exit");
		miSave.setAccelerator(
				KeyStroke.getKeyStroke(KeyEvent.VK_S, Toolkit.getDefaultToolkit().getMenuShortcutKeyMaskEx()));
		miLoad.setAccelerator(
				KeyStroke.getKeyStroke(KeyEvent.VK_O, Toolkit.getDefaultToolkit().getMenuShortcutKeyMaskEx()));
		miSave.addActionListener(e -> doSaveNoPrompt());
		miLoad.addActionListener(e -> doLoadNoPrompt());
		miExit.addActionListener(e -> dispose());
		file.add(miSave);
		file.add(miLoad);
		file.addSeparator();
		file.add(miExit);
		mb.add(file);
		return mb;
	}

	private JToolBar buildToolbar() {
		JToolBar tb = new JToolBar();
		tb.setFloatable(false);
		JButton tSave = new JButton("Save");
		JButton tLoad = new JButton("Load");
		tSave.addActionListener(e -> doSaveNoPrompt());
		tLoad.addActionListener(e -> doLoadNoPrompt());
		tb.add(tSave);
		tb.add(tLoad);
		return tb;
	}

	private void buildUI(JPanel root) {
		transcript.setEditable(false);
		transcript.setLineWrap(true);
		transcript.setWrapStyleWord(true);
		transcript.setFont(new Font(Font.MONOSPACED, Font.PLAIN, 14));
		JScrollPane center = new JScrollPane(transcript);
		center.setBorder(new EmptyBorder(8, 8, 8, 8));

		JPanel dpad = new JPanel(new GridLayout(2, 3, 6, 6));
		dpad.add(btnN);
		dpad.add(btnU);
		dpad.add(new JLabel());
		dpad.add(btnW);
		dpad.add(btnS);
		dpad.add(btnE);

		JPanel bottom = new JPanel(new BorderLayout(8, 8));
		bottom.setBorder(new EmptyBorder(0, 8, 8, 8));
		bottom.add(dpad, BorderLayout.WEST);
		bottom.add(commandField, BorderLayout.CENTER);
		bottom.add(btnDo, BorderLayout.EAST);

		JPanel right = new JPanel(new BorderLayout());
		right.setBorder(new EmptyBorder(8, 8, 8, 8));
		JLabel invTitle = new JLabel("Inventory");
		invTitle.setHorizontalAlignment(SwingConstants.CENTER);
		right.add(invTitle, BorderLayout.NORTH);
		inventoryList.setVisibleRowCount(22);
		right.add(new JScrollPane(inventoryList), BorderLayout.CENTER);

		JPanel top = new JPanel(new BorderLayout());
		status.setBorder(new EmptyBorder(6, 8, 6, 8));
		top.add(status, BorderLayout.CENTER);

		root.add(top, BorderLayout.NORTH);
		root.add(center, BorderLayout.CENTER);
		root.add(bottom, BorderLayout.SOUTH);
		root.add(right, BorderLayout.EAST);

		// Buttons
		btnN.addActionListener(e -> move("North"));
		btnS.addActionListener(e -> move("South"));
		btnW.addActionListener(e -> move("West"));
		btnE.addActionListener(e -> move("East"));
		btnU.addActionListener(e -> move("Up"));
		btnDo.addActionListener(e -> submit());
		commandField.addActionListener(e -> submit());

		// Keyboard arrows
		InputMap im = getRootPane().getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW);
		ActionMap am = getRootPane().getActionMap();
		im.put(KeyStroke.getKeyStroke(KeyEvent.VK_UP, 0), "goN");
		im.put(KeyStroke.getKeyStroke(KeyEvent.VK_DOWN, 0), "goS");
		im.put(KeyStroke.getKeyStroke(KeyEvent.VK_LEFT, 0), "goW");
		im.put(KeyStroke.getKeyStroke(KeyEvent.VK_RIGHT, 0), "goE");
		am.put("goN", new AbstractAction() {
			public void actionPerformed(ActionEvent e) {
				move("North");
			}
		});
		am.put("goS", new AbstractAction() {
			public void actionPerformed(ActionEvent e) {
				move("South");
			}
		});
		am.put("goW", new AbstractAction() {
			public void actionPerformed(ActionEvent e) {
				move("West");
			}
		});
		am.put("goE", new AbstractAction() {
			public void actionPerformed(ActionEvent e) {
				move("East");
			}
		});
	}

	private void hookSystemOut() {
		PrintStream guiOut = new PrintStream(new OutputStream() {
			@Override
			public void write(int b) {
				transcript.append(String.valueOf((char) b));
				transcript.setCaretPosition(transcript.getDocument().getLength());
			}
		}, true);
		System.setOut(guiOut);
		System.setErr(guiOut);
	}

	// -------------- Reflection helpers --------------
	private static Method maybeMethod(Class<?> cls, String name, Class<?>... params) {
		if (cls == null)
			return null;
		try {
			Method m = cls.getDeclaredMethod(name, params);
			m.setAccessible(true);
			return m;
		} catch (Exception ignored) {
			return null;
		}
	}

	private static Field maybeField(Class<?> cls, String name) {
		if (cls == null)
			return null;
		try {
			Field f = cls.getDeclaredField(name);
			f.setAccessible(true);
			return f;
		} catch (Exception ignored) {
			return null;
		}
	}

	private void initReflection() {
		try {
			gameClass = Class.forName("Game");
			worldClass = Class.forName("World");

			// Required basics
			gameMove = maybeMethod(gameClass, "move", String.class);
			gamePrint = maybeMethod(gameClass, "print", Object.class);

			// Optional game helpers
			gameTakeItem = maybeMethod(gameClass, "takeItem", String.class);
			gameCheckI = maybeMethod(gameClass, "checki");
			gameLoadDesc = maybeMethod(gameClass, "loaddescription");

			worldBuildWorld = maybeMethod(worldClass, "buildWorld");
			gameCurrentRoom = maybeField(gameClass, "currentroom");
			if (gameCurrentRoom == null)
				gameCurrentRoom = maybeField(gameClass, "currentRoom");
			gameInventory = maybeField(gameClass, "inventory");

			// Detect Save/Load (try your no-arg methods first)
			gameSave_NoArg = firstNonNull(maybeMethod(gameClass, "saveFile"), maybeMethod(gameClass, "Save") // tolerance
			);
			gameLoad_NoArg = firstNonNull(maybeMethod(gameClass, "loadFile"), maybeMethod(gameClass, "Load") // tolerance
			);
			// And alternate signatures as fallback
			gameSave_File = firstNonNull(maybeMethod(gameClass, "saveGame", File.class),
					maybeMethod(gameClass, "save", File.class));
			gameSave_String = firstNonNull(maybeMethod(gameClass, "saveGame", String.class),
					maybeMethod(gameClass, "save", String.class));
			gameLoad_File = firstNonNull(maybeMethod(gameClass, "loadGame", File.class),
					maybeMethod(gameClass, "load", File.class));
			gameLoad_String = firstNonNull(maybeMethod(gameClass, "loadGame", String.class),
					maybeMethod(gameClass, "load", String.class));

		} catch (Exception ex) {
			append("Reflection init failed: " + ex + "\n");
		}
	}

	@SafeVarargs
	private static <T> T firstNonNull(T... vals) {
		for (T v : vals)
			if (v != null)
				return v;
		return null;
	}

	private void initGameState() {
		try {
			// Build world and set starting room via Game.currentroom if possible
			if (worldBuildWorld != null && gameCurrentRoom != null) {
				Object start = worldBuildWorld.invoke(null);
				gameCurrentRoom.set(null, start);
			}
			// Load descriptions (optional)
			if (gameLoadDesc != null) {
				gameLoadDesc.invoke(null);
			}
			append("Welcome to findmy xbox\n");
			append("Use arrows or type commands. Type 'exit' to quit.\n\n");
			append ("Commands are: use, take, give __ to __ ,read, open, close, destroy, talk, and clean ");
			append ("You may also save and load\n");
		} catch (Exception ex) {
			append("Failed to initialize game state: " + ex + "\n");
		}
	}

	private void printCurrentRoomOnce() {
		try {
			if (gameCurrentRoom != null && gamePrint != null) {
				Object current = gameCurrentRoom.get(null);
				gamePrint.invoke(null, current);
			}
		} catch (Exception ex) {
			append("(Couldn't print current room: " + ex + ")\n");
		}
	}

	private void submit() {
		String cmd = commandField.getText().trim();
		if (cmd.isEmpty())
			return;
		append("> " + cmd + "\n");
		if (cmd.equalsIgnoreCase("exit")) {
			dispose();
			return;
		}
		Game.runGame(cmd);
		// Basic parser
//		String[] parts = cmd.split(" ", 2);
//		String action = parts[0];
//		String argument = (parts.length > 1) ? parts[1] : null;
//
//		if (equalsAnyIgnoreCase(action, "north", "south", "east", "west", "up")) {
//			move(cap(action));
//		} else if (equalsAnyIgnoreCase(action, "look", "l")) {
//			printCurrentRoomOnce();
//		} else if (action.equalsIgnoreCase("take")) {
//			take(argument);
//		} else if (action.equalsIgnoreCase("i")) {
//			showInventory();
//		} else if (action.equalsIgnoreCase("save")) {
//			doSaveNoPrompt();
//		} else if (action.equalsIgnoreCase("load")) {
//			doLoadNoPrompt();
//		} else if (action.equalsIgnoreCase("use")) {
//			use(argument);
//		} else {
//			// Try passing the raw command to move (so "North" works)
//			move(cmd);
//		}
		commandField.setText("");
		refreshInventoryBox();
	}

	private boolean equalsAnyIgnoreCase(String s, String... opts) {
		for (String o : opts)
			if (s.equalsIgnoreCase(o))
				return true;
		return false;
	}

	private String cap(String s) {
		return s.substring(0, 1).toUpperCase() + s.substring(1).toLowerCase();
	}

	private void move(String dir) {
		try {
			if (gameMove == null) {
				append("(move failed: Game.move(String) not found)\n");
				return;
			}
			gameMove.invoke(null, dir);

			// After moving, print the new room (if printable)
			if (gameCurrentRoom != null && gamePrint != null) {
				Object current = gameCurrentRoom.get(null);
				gamePrint.invoke(null, current);
			}
		} catch (Exception ex) {
			append("(move failed: " + ex + ")\n");
		}
	}

	private void take(String what) {
		try {
			if (what == null || what.isEmpty()) {
				if (gamePrint != null)
					gamePrint.invoke(null, "What would you like to take?");
				else
					append("What would you like to take?\n");
				return;
			}
			if (gameTakeItem != null) {
				gameTakeItem.invoke(null, what);
			} else {
				// Fallback local inventory
				String cleaned = what.trim().toLowerCase();
				if (!guiInventoryFallback.contains(cleaned)) {
					guiInventoryFallback.add(cleaned);
					append("You take the " + what.trim() + ".\n");
				} else {
					append("You already have the " + what.trim() + ".\n");
				}
			}
		} catch (Exception ex) {
			append("(take failed: " + ex + ")\n");
		}
	}

	private void showInventory() {
		try {
			if (gameCheckI != null) {
				gameCheckI.invoke(null);
				return;
			}
			java.util.List<String> items = getInventorySnapshot();
			if (items.isEmpty())
				append("Inventory is empty.\n");
			else {
				append("Inventory:\n");
				for (String it : items)
					append(" - " + it + "\n");
			}
		} catch (Exception ex) {
			append("(inventory check failed: " + ex + ")\n");
		}
	}

	private void refreshInventoryBox() {
		try {
			inventoryModel.clear();
			for (String it : getInventorySnapshot())
				inventoryModel.addElement(it);
		} catch (Exception ignored) {
		}
	}

	@SuppressWarnings("unchecked")
	private java.util.List<String> getInventorySnapshot() {
		try {
			if (gameInventory != null) {
				Object invObj = gameInventory.get(null);
				if (invObj instanceof java.util.List) {
					java.util.List<?> list = (java.util.List<?>) invObj;
					java.util.List<String> out = new ArrayList<>(list.size());
					for (Object o : list)
						out.add(String.valueOf(o));
					return out;
				}
			}
		} catch (Exception ignored) {
		}
		return new ArrayList<>(guiInventoryFallback);
	}

	// ---------- Save / Load (no prompts) ----------
	private void doSaveNoPrompt() {
		boolean ok = invokeSave(); // will use your Game.saveFile() if present
		if (ok)
			append("[Saved]\n");
		else
			append("(Save failed — no compatible save method found in Game)\n");
	}

	private void doLoadNoPrompt() {
		boolean ok = invokeLoad(); // will use your Game.loadFile() if present
		if (ok) {
			append("[Loaded]\n");
			printCurrentRoomOnce();
			refreshInventoryBox();
		} else {
			append("(Load failed — no compatible load method found in Game)\n");
		}
	}

	private boolean invokeSave() {
		try {
			if (gameSave_NoArg != null) {
				gameSave_NoArg.invoke(null);
				return true;
			} // your method
			if (gameSave_File != null) {
				gameSave_File.invoke(null, new File("save0.sav"));
				return true;
			}
			if (gameSave_String != null) {
				gameSave_String.invoke(null, "save0.sav");
				return true;
			}
		} catch (InvocationTargetException ite) {
			append("(Save error: " + ite.getTargetException() + ")\n");
		} catch (Exception ex) {
			append("(Save error: " + ex + ")\n");
		}
		return false;
	}

	private boolean invokeLoad() {
		try {
			if (gameLoad_NoArg != null) {
				gameLoad_NoArg.invoke(null);
				return true;
			} // your method
			if (gameLoad_File != null) {
				gameLoad_File.invoke(null, new File("save0.sav"));
				return true;
			}
			if (gameLoad_String != null) {
				gameLoad_String.invoke(null, "save0.sav");
				return true;
			}
		} catch (InvocationTargetException ite) {
			append("(Load error: " + ite.getTargetException() + ")\n");
		} catch (Exception ex) {
			append("(Load error: " + ex + ")\n");
		}
		return false;
	}

	// ---------- misc ----------
	public void append(String text) {
		transcript.append(text);
		transcript.setCaretPosition(transcript.getDocument().getLength());
	}

	public static void main(String[] args) {
		SwingUtilities.invokeLater(() -> new GameSwingGUI().setVisible(true));
	}
}
