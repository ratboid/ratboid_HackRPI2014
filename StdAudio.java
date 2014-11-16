/*************************************************************************
 *  Compilation:  javac StdAudio.java
 *  Execution:    java StdAudio
 *  
 *  Simple library for reading, writing, and manipulating .wav files.

 *
 *  Limitations
 *  -----------
 *    - Does not seem to work properly when reading .wav files from a .jar file.
 *    - Assumes the audio is monaural, with sampling rate of 44,100.
 *
 *************************************************************************/

import java.applet.*;
import java.io.*;
import java.net.*;
import javax.sound.sampled.*;
import java.util.*;

/**
 *  <i>Standard audio</i>. This class provides a basic capability for
 *  creating, reading, and saving audio. 
 *  <p>
 *  The audio format uses a sampling rate of 44,100 (CD quality audio), 16-bit, monaural.
 *
 *  <p>
 *  For additional documentation, see <a href="http://introcs.cs.princeton.edu/15inout">Section 1.5</a> of
 *  <i>Introduction to Programming in Java: An Interdisciplinary Approach</i> by Robert Sedgewick and Kevin Wayne.
 *
 *  @author Robert Sedgewick
 *  @author Kevin Wayne
 */
public final class StdAudio {

    /**
     *  The sample rate - 44,100 Hz for CD quality audio.
     */
    public static final int SAMPLE_RATE = 44100;

    private static final int BYTES_PER_SAMPLE = 2;                // 16-bit audio
    private static final int BITS_PER_SAMPLE = 16;                // 16-bit audio
    private static final double MAX_16_BIT = Short.MAX_VALUE;     // 32,767
    private static final int SAMPLE_BUFFER_SIZE = 4096;


    private static SourceDataLine line;   // to play the sound
    private static byte[] buffer;         // our internal buffer
    private static int bufferSize = 0;    // number of samples currently in internal buffer
	
	public static Set<Tone> tones = new TreeSet<Tone>();
	private static double phase = 0.0;
	private static double STEP = 0.01;
	
	public enum Instrument {SINE, SQUARE, SAWTOOTH};

    // do not instantiate
    private StdAudio() { }

   
    // static initializer
    static { init(); }

    // open up an audio stream
    private static void init() {
        try {
            // 44,100 samples per second, 16-bit audio, mono, signed PCM, little Endian
            AudioFormat format = new AudioFormat((float) SAMPLE_RATE, BITS_PER_SAMPLE, 1, true, false);
            DataLine.Info info = new DataLine.Info(SourceDataLine.class, format);

            line = (SourceDataLine) AudioSystem.getLine(info);
            line.open(format, SAMPLE_BUFFER_SIZE * BYTES_PER_SAMPLE);
            
            // the internal buffer is a fraction of the actual buffer size, this choice is arbitrary
            // it gets divided because we can't expect the buffered data to line up exactly with when
            // the sound card decides to push out its samples.
            buffer = new byte[SAMPLE_BUFFER_SIZE * BYTES_PER_SAMPLE/3];
        } catch (Exception e) {
            System.out.println(e.getMessage());
            System.exit(1);
        }

        // no sound gets made before this call
        line.start();
    }


    /**
     * Close standard audio.
     */
    public static void close() {
        line.drain();
        line.stop();
    }
    
    /**
     * Write one sample (between -1.0 and +1.0) to standard audio. If the sample
     * is outside the range, it will be clipped.
     */
    public static void play(double in) {

        // clip if outside [-1, +1]
        if (in < -1.0) in = -1.0;
        if (in > +1.0) in = +1.0;

        // convert to bytes
        short s = (short) (MAX_16_BIT * in);
        buffer[bufferSize++] = (byte) s;
        buffer[bufferSize++] = (byte) (s >> 8);   // little Endian

        // send to sound card if buffer is full        
        if (bufferSize >= buffer.length) {
            line.write(buffer, 0, buffer.length);
            bufferSize = 0;
        }
    }

    /**
     * Write an array of samples (between -1.0 and +1.0) to standard audio. If a sample
     * is outside the range, it will be clipped.
     */
    public static void play(double[] input) {
        for (int i = 0; i < input.length; i++) {
            play(input[i]);
        }
    }

    // create a sound (sine, square or other wave) of the given frequency (Hz), for the given
    // duration (seconds) scaled to the given volume (amplitude)
    private static double[] note(double hz, double duration, double amplitude, double shift, Instrument instrument) {
        int N = (int) (StdAudio.SAMPLE_RATE * duration);
		int D = (int) (StdAudio.SAMPLE_RATE * shift);
        double[] a = new double[N+1];
        for (int i = 0; i <= N; i++){
			double k = (i + D) * hz / StdAudio.SAMPLE_RATE;
			switch(instrument){
				case SINE:
					a[i] = amplitude * Math.sin(2 * Math.PI * k);
					break;
				case SQUARE:
					a[i] = amplitude * Math.pow(-1.0, Math.floor(k));
					break;
				case SAWTOOTH:
					a[i] = amplitude * (k - Math.floor(k));
					break;
				
			}
		}
        return a;
    }

	private static double[] sum(double[] ... waves){
		int max = waves[0].length;
		for(int i = 0; i < waves.length; i++){
			if(waves[i].length > max)
				max = waves[i].length;
		}
		double[] result = new double[max];
		for(int t = 0; t < result.length; t++){
			result[t] = 0.0;
			for(int w = 0; w < waves.length; w++){
				if(t < waves[w].length){
					result[t] += waves[w][t];
				}
			}
			result[t] = result[t]/waves.length;
		}
		return result;

	}
	
	public static double freq(double f0,double d){
		return f0 * Math.pow(2,d/12.0);
	}
	
	public static void add(Tone t){
		tones.add(t);
		playInput();
	}
	
	public static void remove(Tone t){
		tones.remove(t);
		playInput();
	}
	
	public static void playInput(){
		double[][] tune = new double[tones.size()][];
		int i = 0;
		for(Tone t: tones){
			tune[i] = StdAudio.note(t.getFrequency(),STEP,0.5,phase,t.getInstrument());
			i++;
		}
		if(tune.length == 0){
			phase = 0.0;
		}else{
			StdAudio.play(StdAudio.sum(tune));
			phase += STEP;
		}
	}
	
    public static void main(String[] args) {
		
		Scanner scan = new Scanner(System.in);
		String[] line = null;
		double[][] tune;
		boolean flag = true;
		while(flag){
			line = scan.nextLine().toLowerCase().split("\\W+");
			tune = new double[line.length][];
			for(int w = 0; w < line.length; w++ ){
				String word = line[w];
				word.replaceAll("[^a-z]","");
				if(word.equals("quit")){
					flag = false;
				}
				double[][] sounds = new double[word.length()][];
				for(int i = 0; i < word.length(); i++){
					int d = (int)word.charAt(i) - (int)'m'; 
					sounds[i] = StdAudio.sum(
								StdAudio.note(freq(440,d),0.5,0.5,0.0,Instrument.SINE));
				}
				if(word.length() > 0){
					tune[w] = StdAudio.sum(sounds);
				}
			}
			for(int t = 0; t < tune.length; t++){
				StdAudio.play(tune[t]);
			}
		}
		
        // need to call this in non-interactive stuff so the program doesn't terminate
        // until all the sound leaves the speaker.
        StdAudio.close(); 

        // need to terminate a Java program with sound
        System.exit(0);
    }
}
