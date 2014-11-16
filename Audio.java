import java.applet.*;
import java.io.*;
import java.net.*;
import javax.sound.sampled.*;
import java.util.*;

/**
 *
 *  @author Robert Sedgewick
 *  @author Kevin Wayne
 *	@author Arthur Kalb
 */
public final class Audio {

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
	


    // do not instantiate
    private Audio() { }
   
    // static initializer
    static { init(); }

    // open up an audio stream
    private static void init() {
        try {
        AudioFormat format = new AudioFormat((float) SAMPLE_RATE, BITS_PER_SAMPLE, 1, true, false);
            DataLine.Info info = new DataLine.Info(SourceDataLine.class, format);

            line = (SourceDataLine) AudioSystem.getLine(info);
            line.open(format, SAMPLE_BUFFER_SIZE * BYTES_PER_SAMPLE);

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

	/**
		BEGIN ARTHUR CODE
	*/
	
	public static Set<Tone> tones = new TreeSet<Tone>();
	private static double phase = 0.0;
	private static double STEP = 0.001;
	
	public enum Instrument {SINE, SQUARE, SAWTOOTH};
	
    // create a sound (sine, square or other wave) of the given frequency (Hz), for the given
    // duration (seconds) scaled to the given volume (amplitude)
    private static double[] note(double hz, double duration, double amplitude, double shift, Instrument instrument) {
        int N = (int) (Audio.SAMPLE_RATE * duration);
		int D = (int) (Audio.SAMPLE_RATE * shift);
        double[] a = new double[N+1];
        for (int i = 0; i <= N; i++){
			double k = (i + D) * hz / Audio.SAMPLE_RATE;
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
		synchronized(tones){
			tones.add(t);
		}
	}
	
	public static void remove(Tone t){
		synchronized(tones){
			tones.remove(t);
		}
	}
	
	public static void playInput(){
		synchronized(tones){
			double[][] tune = new double[tones.size()][];
			int i = 0;
			for(Tone t: tones){
				tune[i] = Audio.note(t.getFrequency(),STEP,0.5,phase,t.getInstrument());
				i++;
			}
			if(tune.length == 0){
				phase = 0.0;
			}else{
				Audio.play(Audio.sum(tune));
				phase += STEP;
			}
		}
	}
}
