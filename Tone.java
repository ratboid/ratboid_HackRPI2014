import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import javax.swing.event.*;
import java.util.*;

/*
	Container for tones, with both frequency and wave information
*/
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
		if(Math.abs(this.frequency-t.frequency) < 1E-6){
			if(this.instrument.ordinal() > t.instrument.ordinal()){
				return 1;
			}else if(this.instrument.ordinal() < t.instrument.ordinal()){
				return -1;
			}else{
				return 0;
			}
		}else if(this.frequency > t.frequency){
			return 1;
		}else{
			return -1;
		}
	}
	
	public boolean equals(Object o){
		return (o instanceof Tone && this.compareTo((Tone)o) == 0);
	}
}