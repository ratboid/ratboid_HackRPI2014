import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import javax.swing.event.*;
import java.util.*;

public class Chiptune extends JFrame{
	JPanel panel;
	Vector<KeyMapper> menus;

	public Chiptune(){
		panel = new JPanel(new GridLayout(6,6));
		menus = new Vector<KeyMapper>();
		
		for(int i = KeyEvent.VK_0, j = 0; i <= KeyEvent.VK_Z;i++, j += 3){
			if( i == KeyEvent.VK_9 + 1){
				i = KeyEvent.VK_A;
			}
			this.add(new KeyMapper(i,j));
		}
		
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
		this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		this.setSize(980, 260); //980, 260 or 800, 500
		this.setTitle("ChipTune");
		this.setVisible(true);
		while(true){
			Audio.playInput();
		}
	}
	
	private void add(KeyMapper km){
		JPanel p = new JPanel(new FlowLayout());
		p.add(km.getLabel());
		p.add(km.getComboBox());
		panel.add(p);
		menus.add(km);
	}
	
	private Tone getTone(int ch){
		for(KeyMapper k: menus){
			if(k.getKey() == ch){
				return k.getTone();
			}
		}
		return null;
	}
	
	public static void main(String[] args){
		new Chiptune();
		
	}
}