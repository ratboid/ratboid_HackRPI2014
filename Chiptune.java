import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import javax.swing.event.*;
import java.util.*;
import java.io.*;

public class Chiptune extends JFrame{
	JPanel panel;
	JMenu menu;
	JMenuItem saveFile;
	Vector<KeyMapper> menus;
	
	public Chiptune(){
		this(null);
	}

	public Chiptune(String file){
		panel = new JPanel(new GridLayout(6,6));
		menus = new Vector<KeyMapper>();
		
		//Create Window components
		if(file == null){
			for(int i = KeyEvent.VK_0, j = 0; i <= KeyEvent.VK_Z;i++, j++){
				if( i == KeyEvent.VK_9 + 1){
					i = KeyEvent.VK_A;
				}
				this.add(new KeyMapper(i,j));
			}
		}else{
			try{
				Scanner scan = new Scanner(new File(file));	
				KeyMapper.BASE_TONE = Double.parseDouble(scan.nextLine());
				scan.useDelimiter(",");
				while(scan.hasNext()){
					String[] args = scan.next().split(":");
					if(args.length >= 2){
						try{
							this.add(new KeyMapper((int)args[0].charAt(0),Integer.parseInt(args[1])));
						}catch(ArrayIndexOutOfBoundsException e){
							System.out.println(file+" has invalid arguments for one combination");
						}catch(NumberFormatException e){
							System.out.println("cannot acceses index: "+args[1]);
						}catch(Exception e){
							System.out.println("Unaccounted for Exception "+ e);
						}
					}
				}
			}
			catch(FileNotFoundException e){
				System.out.println(e);
			}catch(NullPointerException e){
				System.out.println("bad file!");
			}
		}
		
		//Handle Key Strokes
		panel.addKeyListener(new KeyListener(){
			public void keyPressed(KeyEvent e){
				Tone t = getTone(e.getExtendedKeyCode());
				if(t!=null)
					Audio.add(t);
			}
			public void keyReleased(KeyEvent e){
				Tone t = getTone(e.getExtendedKeyCode());
				if(t!=null)
					Audio.remove(t);
			}
			public void keyTyped(KeyEvent e){
			}
			
		});
		panel.setFocusable(true);
		this.add(panel);
		
		//handle menu bar stuff
		menu = new JMenu("File");
		saveFile = new JMenuItem("Save Configuration");
		saveFile.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent e){
				final JFileChooser fc = new JFileChooser();
				int returnVal = fc.showSaveDialog(panel);
				
				if(returnVal == JFileChooser.APPROVE_OPTION){
					File file = fc.getSelectedFile();
					PrintWriter out = null;
					try{
						out = new PrintWriter(new BufferedWriter(new FileWriter(file)));
						out.println(KeyMapper.BASE_TONE);
						for(KeyMapper km : menus){
							out.print(km.getLabel().getText()+":"+km.getComboBox().getSelectedIndex()+",");
						}
					}catch(IOException io){
						System.out.println("I/O Exception");
					}finally{
						if(out != null)
							out.close();
					}
				}
			}
		});
		this.setJMenuBar(new JMenuBar());
		this.getJMenuBar().add(menu);
		menu.add(saveFile);
		
		this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		this.setSize(980, 260); //980, 260 or 800, 500
		this.setTitle("ChipTune");
		this.setVisible(true);
	}
	
	/*
		Handles the insertion of an individual KeyMapper
	*/
	private void add(KeyMapper km){
		JPanel p = new JPanel(new FlowLayout());
		p.add(km.getLabel());
		p.add(km.getComboBox());
		panel.add(p);
		menus.add(km);
	}
	
	/*
		Extracts Tone from the list
	*/
	private Tone getTone(int ch){
		for(KeyMapper k: menus){
			if(k.getKey() == ch){
				return k.getTone();
			}
		}
		return null;
	}
	
	public static void main(String[] args){
		if(args.length > 0){
			new Chiptune(args[0]);
		}else{
			new Chiptune();
		}
		while(true){
			Audio.playInput();
		}
		
	}
}