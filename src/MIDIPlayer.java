
/**
 * Created by garethmcfarlane on 19/05/15.
 * SID 430172980
 *
 */


import javax.sound.midi.*;


public class MIDIPlayer
{


    private static final int PERCUSSION_CHANNEL = 9;


    private static final int VELOCITY = 127;         



    private static String[] instrumentNames = {
            "Open Hi-Hat", "Acoustic Snare", "Crash Cymbal", "Hand Clap", "Bass Drum",
            "Closed Hi-Hat", "Low-mid Tom", "High Agogo", "Open Hi Conga"
    };

    private int[] instrumentKeys = {
            46, 38, 49, 39, 35,
            42, 47, 67, 37
    };   




    private Synthesizer synthesizer = null;
    private MidiChannel channel = null;     


    public MIDIPlayer()
    {
        try {
            synthesizer = MidiSystem.getSynthesizer();
            synthesizer.open();
            channel = synthesizer.getChannels()[PERCUSSION_CHANNEL];

        }
        catch(MidiUnavailableException e) {
            System.out.println("Cannot initialize MIDI synthesizer");
            System.exit(1);
        }
    }  



    public static String getInstrumentName(int i)
    // used by DrumManager
    {
        if (i < 0) {
            i = Math.abs(i)%instrumentNames.length;
            System.out.println("Name index cannot be negative; using " + i);

        }
        else if (i >= instrumentNames.length) {
            i = i%instrumentNames.length;
            System.out.println("Name index too large; using " + i);
        }

        return instrumentNames[i];
    }  

    synchronized public void drumOn(String name)
    {
        int key = name2Key(name);
        if ((channel != null) && (key != -1))
            channel.noteOn(key, VELOCITY);
    }  

    synchronized public void drumOff(String name)
    {
        int key = name2Key(name);
        if ((channel != null) && (key != -1))
            channel.noteOff(key);
    }  

    private int name2Key(String name)
    // convert an instrument name to its MIDI percussion key
    {
        for (int i=0; i < instrumentNames.length; i++)
            if (instrumentNames[i].equals(name))
                return instrumentKeys[i];
        return -1;
    }  


    synchronized public void close()
    {
        if (channel != null) {
            channel.allNotesOff();
            channel = null;
        }
        if (synthesizer != null) {
            synthesizer.close();
            synthesizer = null;
        }
    }  

    private void wait(int delay)
    {
        try {
            Thread.sleep(delay);
        }
        catch (InterruptedException e) {}
    }  





}