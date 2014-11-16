import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import javax.swing.event.*;
import java.util.*;

class Tone implements Comparable<Tone>{
	private double frequency;
	private Audio.Instrument instrument;
	
	public Tone(double f, Audio.Instrument i){
		this.frequency = f;
		this.instrument = i;
	}
	
	public double getFrequency(){
		return frequency;
	}
	
	public Audio.Instrument getInstrument(){
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