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
	private static final double BASE_TONE = 110;
	
	private static void init(){
		if(standardTones == null){
			standardTones = new Tone[ (TONE_COUNT + 1)* insts.length];
			for(int i = 0; i <= TONE_COUNT; i++){
				for(int j = 0; j < insts.length; j++){
					standardTones[j * TONE_COUNT + i] = new Tone(Audio.freq(BASE_TONE,i),insts[j]); 
				}
			}
		}
	}

	public KeyMapper(int key,int index,Tone[] tones){
		init();
		if(tones == null){
			tones = standardTones;
		}
		this.key = key;
		this.label = new JLabel(""+(char)key);
		this.comboBox = new JComboBox(tones);
		this.comboBox.setFocusable(false);
		comboBox.setSelectedIndex(index);
	}
	
	public KeyMapper(int key, int index){
		this(key,index,standardTones);
	}
	/**
	public KeyMapper(int key,double hz,Audio.Instrument tone){
		
	}*/
	
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
