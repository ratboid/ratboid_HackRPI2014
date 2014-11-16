import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import javax.swing.event.*;
import java.util.*;

/*
	Container for GUI information and mappings for the keys
*/
class KeyMapper{
	private JComboBox<Tone> comboBox;
	private JLabel label;
	private int key;
	private static Tone[] standardTones = null;
	private static final int TONE_COUNT = 36;
	private static final Audio.Instrument[] insts = Audio.Instrument.values();
	public KeyMapper(int key,int index){
		if(standardTones == null){
			standardTones = new Tone[ (TONE_COUNT + 1)* insts.length];
			for(int i = 0; i <= TONE_COUNT; i++){
				for(int j = 0; j < insts.length; j++){
					standardTones[i * insts.length + j] = new Tone(Audio.freq(220,i),insts[j]); 
				}
			}
		}
		
		this.key = key;
		this.label = new JLabel(""+(char)key);
		this.comboBox = new JComboBox(standardTones);
		this.comboBox.setFocusable(false);
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
