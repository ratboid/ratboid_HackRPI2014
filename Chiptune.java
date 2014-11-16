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
		
		for(int i = KeyEvent.VK_0, j = 2; i <= KeyEvent.VK_Z;i++, j += 3){
			if( i == KeyEvent.VK_9 + 1){
				i = KeyEvent.VK_A;
			}
			this.add(new KeyMapper(i,j));
		}
		
		panel.addKeyListener(new KeyListener(){
			public void keyPressed(KeyEvent e){
				StdAudio.add(getTone(e.getExtendedKeyCode()));
				System.out.print(e.getExtendedKeyCode()+" ");
				System.out.println(StdAudio.tones.size());
			}
			public void keyReleased(KeyEvent e){
				StdAudio.remove(getTone(e.getExtendedKeyCode()));
				System.out.print("-"+e.getExtendedKeyCode()+" ");
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
		while(true){
			StdAudio.playInput();
		}
	}
}

class KeyMapper{
	private JComboBox<Tone> comboBox;
	private JLabel label;
	private int key;
	private static Tone[] standardTones = null;
	private static final int TONE_COUNT = 36;
	private static final StdAudio.Instrument[] insts = StdAudio.Instrument.values();
	public KeyMapper(int key,int index){
		if(standardTones == null){
			standardTones = new Tone[ (TONE_COUNT + 1)* insts.length];
			for(int i = 0; i <= TONE_COUNT; i++){
				for(int j = 0; j < insts.length; j++){
					standardTones[i * insts.length + j] = new Tone(StdAudio.freq(220,i),insts[j]); 
				}
			}
		}
		
		this.key = key;
		this.label = new JLabel(""+(char)key);
		this.comboBox = new JComboBox(standardTones);
		comboBox.setSelectedIndex(index);
		
		
	}
	
	public JComboBox<Tone> getComboBox(){
		return comboBox;
	}
	
	public JLabel getLabel(){
		return label;
	}
	
	public Tone getTone(){
		return (Tone)comboBox.getSelectedItem();
	}
	
	public int getKey(){
		return key;
	}
}

class Tone implements Comparable<Tone>{
	private double frequency;
	private StdAudio.Instrument instrument;
	
	public Tone(double f, StdAudio.Instrument i){
		this.frequency = f;
		this.instrument = i;
	}
	
	public double getFrequency(){
		return frequency;
	}
	
	public StdAudio.Instrument getInstrument(){
		return instrument;
	}
	
	public String toString(){
		return instrument.name() + " : " + Math.round(frequency);
	}
	
	public int compareTo(Tone t){
		if(this.frequency>t.frequency){
			return -1;
		}else if(t.frequency>this.frequency){
			return 1;
		}else{
			return 0;
		}
	}
}