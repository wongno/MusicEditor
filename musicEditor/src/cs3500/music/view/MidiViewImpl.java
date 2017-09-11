package cs3500.music.view;

import java.util.ArrayList;

import javax.sound.midi.InvalidMidiDataException;
import javax.sound.midi.MidiEvent;
import javax.sound.midi.MidiMessage;
import javax.sound.midi.MidiSystem;
import javax.sound.midi.MidiUnavailableException;
import javax.sound.midi.Sequence;
import javax.sound.midi.Sequencer;
import javax.sound.midi.ShortMessage;
import javax.sound.midi.Synthesizer;
import javax.sound.midi.Track;

import cs3500.music.model.MusicNote;
import cs3500.music.model.ViewModel;

/**
 * A skeleton for MIDI playback.
 */
public class MidiViewImpl implements IMusicView {
  private final Synthesizer synth;
  private final Sequencer sequencer;
  private boolean pause;
  private int tempo;
  private static final int tickBeat = 10;
  private int tickPos;

  /**
   * To construct a midi view object.
   */
  public MidiViewImpl() {
    Synthesizer sy = null;
    Sequencer s = null;
    try {
      sy = MidiSystem.getSynthesizer();
      sy.loadAllInstruments(sy.getDefaultSoundbank());
      sy.open();
      s = MidiSystem.getSequencer();
      s.open();
    } catch (MidiUnavailableException e) {
      e.printStackTrace();
    }
    synth = sy;
    sequencer = s;
    pause = false;
  }

  /**
   * to construct a midi view object for testing purposes.
   *
   * @param s the given sequencer for testing
   */
  public MidiViewImpl(Sequencer s) {
    Synthesizer sy = null;
    try {
      sy = MidiSystem.getSynthesizer();
      sy.loadAllInstruments(sy.getDefaultSoundbank());
      sy.open();
      s.open();
    } catch (MidiUnavailableException e) {
      e.printStackTrace();
    }
    synth = sy;
    this.sequencer = s;
  }

  /**
   * Relevant classes and methods from the javax.sound.midi library:
   * <ul>
   * <li>{@link MidiSystem#getSynthesizer()}</li>
   * <li>{@link Synthesizer}
   * <ul>
   * <li>{@link Synthesizer#open()}</li>
   * <li>{@link Synthesizer#getReceiver()}</li>
   * <li>{@link Synthesizer#getChannels()}</li>
   * </ul>
   * </li>
   * <li>{@link Receiver}
   * <ul>
   * <li>{@link Receiver#send(MidiMessage, long)}</li>
   * <li>{@link Receiver#close()}</li>
   * </ul>
   * </li>
   * <li>{@link MidiMessage}</li>
   * <li>{@link ShortMessage}</li>
   * <li>{@link MidiChannel}
   * <ul>
   * <li>{@link MidiChannel#getProgram()}</li>
   * <li>{@link MidiChannel#programChange(int)}</li>
   * </ul>
   * </li>
   * </ul>
   *
   * @see <a href="https://en.wikipedia.org/wiki/General_MIDI">
   *   https://en.wikipedia.org/wiki/General_MIDI
   * </a>
   */

  /**
   * to convert a note's pitch and octave fields into a single value represented by an int.
   *
   * @param n the given MusicNote
   * @return the pitch and octave as an integer value
   */
  private int pitchOctaveIntValue(MusicNote n) {
    int p = n.getPitch().ordinal();
    int o = (1 + n.getOctave()) * 12;
    return Math.abs(p + o);
  }

  /**
   * .
   *
   * @param model contains the data that will be "drawn"
   * @throws InvalidMidiDataException if messages and events are not initialized properly
   */
  @Override
  public void drawModel(ViewModel model) throws InvalidMidiDataException {
    Sequence s = null;
    ArrayList<MusicNote> a = model.allNotes();
    tempo = model.getTempo();
    sequencer.setTempoInMPQ(tempo);
    try {
      s = new Sequence(Sequence.PPQ, tickBeat);
    } catch (InvalidMidiDataException e) {
      e.printStackTrace();
    }
    Track track = s.createTrack();
    for (MusicNote n : a) {
      MidiMessage startMessage = new ShortMessage(ShortMessage.NOTE_ON, 0, pitchOctaveIntValue(n),
              n.getVolume());
      MidiMessage stopMessage = new ShortMessage(ShortMessage.NOTE_OFF, 0, pitchOctaveIntValue(n),
              n.getVolume());
      MidiMessage instrumentMessage = new ShortMessage(ShortMessage.PROGRAM_CHANGE, 0, 0,
              n.getInstrument());
      MidiEvent startEvent = new MidiEvent(startMessage, n.getStartBeat() * tickBeat);
      MidiEvent stopEvent = new MidiEvent(stopMessage, (n.getStartBeat() + n.getDuration() + 1)
              * tickBeat);
      MidiEvent instrumentEvent = new MidiEvent(instrumentMessage, n.getStartBeat() * tickBeat);
      track.add(instrumentEvent);
      track.add(startEvent);
      track.add(stopEvent);
    }
    sequencer.setSequence(s);
    sequencer.setTickPosition(0);
    sequencer.setTempoInMPQ(tempo);
    sequencer.start();
  }

  /**
   * .
   */
  public void jumpHome() {
    if (sequencer.isRunning()) {
      sequencer.stop();
    }
    sequencer.setTickPosition(0);
    sequencer.start();
    sequencer.setTempoInMPQ(tempo);
  }

  /**
   * .
   */
  public void jumpEnd() {
    if (sequencer.isRunning()) {
      sequencer.stop();
    }
    sequencer.setTickPosition(sequencer.getTickLength());
    sequencer.start();
    sequencer.setTempoInMPQ(tempo);
  }

  /**
   * .
   */
  public void pauseMusic() {
    pause = !pause;
    if (pause) {
      sequencer.stop();
    } else {
      sequencer.start();
      sequencer.setTempoInMPQ(tempo);
    }
  }

  /**
   * to get the tickpos field.
   *
   * @return int
   */
  public int getTick() {
    tickPos = (int) sequencer.getTickPosition();
    return tickPos;
  }

  /**
   * to get the pause field.
   *
   * @return boolean
   */
  public boolean getPause() {
    return pause;
  }

}
