package cs3500.music.model;


import java.util.ArrayList;
import java.util.HashMap;
import java.util.Set;

import cs3500.music.util.CompositionBuilder;

/**
 * to represent the fully functional version of the model for the Music Editor.
 */
public class MusicModel implements IMusicModel {

  protected HashMap<Integer, ArrayList<MusicNote>> noteList; // can have multiple notes associated
  // with a single startBeat
  protected int beatsPerMeasure; // must be greater than 0
  protected int tempo;

  /**
   * constructor for the MusicModel.
   *
   * @param beatsPerMeasure the number of beats per measure in this model
   * @param tempo           the tempo this model
   * @throws IllegalArgumentException if the beatsPerMeasure is not greater than 0
   */
  public MusicModel(int beatsPerMeasure, int tempo) {
    if (beatsPerMeasure <= 0) {
      throw new IllegalArgumentException("Invalid BPM.");
    }
    this.noteList = new HashMap<Integer, ArrayList<MusicNote>>();
    this.beatsPerMeasure = beatsPerMeasure;
    this.tempo = tempo;
  }

  /**
   * convenience constructor for the MusicModel.
   * sets beatsPerMeasure to 4, a common amount.
   * sets tempo to 200000.
   */
  public MusicModel() {
    this.noteList = new HashMap<Integer, ArrayList<MusicNote>>();
    this.beatsPerMeasure = 4;
    this.tempo = 200000;
  }

  /**
   * .
   *
   * @param n the MusicNote to be added
   * @throws IllegalArgumentException if the note is invalid
   */
  @Override
  public void addNote(MusicNote n) {
    for (int i = n.startBeat; i < n.startBeat + n.duration; i = i + 1) {
      if (noteList.isEmpty()) {
        noteList.put(i, new ArrayList<MusicNote>());
        noteList.get(i).add(n);
      } else if (!noteList.containsKey(i)) {
        noteList.put(i, new ArrayList<MusicNote>());
        noteList.get(i).add(n);
      } else {
        noteList.get(i).add(n);
        noteList.put(i, noteList.get(i));
      }
    }
  }

  /**
   * .
   *
   * @param an the list of MusicNotes to be added
   * @throws IllegalArgumentException if a note in the list is invalid
   */
  @Override
  public void addNotes(ArrayList<MusicNote> an) {
    for (MusicNote mn : an) {
      addNote(mn);
    }
  }

  /**
   * To return all of the notes in this model as an ArrayList.
   *
   * @return a list of all the notes that make up the model
   */
  public ArrayList<MusicNote> allNotes() {
    ArrayList<MusicNote> a = new ArrayList<MusicNote>();
    if (!noteList.isEmpty()) {
      for (Integer i : noteList.keySet()) {
        for (MusicNote m : noteList.get(i)) {
          if (m.isStartOfNote(i) && !a.contains(m)) {
            a.add(m);
          }
        }
      }
    }
    return a;
  }

  /**
   * .
   *
   * @param n the note to be removed
   * @throws IllegalArgumentException if the note is invalid or if it does not exist in the
   *                                  noteList
   */
  @Override
  public void removeNote(MusicNote n) {
    if (allNotes().contains(n)) {
      for (int i = n.startBeat; i < n.startBeat + n.duration; i = i + 1) {
        noteList.get(i).remove(n);
        if (noteList.get(i).isEmpty()) {
          noteList.remove(i);
        }
      }
    } else {
      throw new IllegalArgumentException("Note Does Not Exist");
    }
  }

  /**
   * .
   *
   * @param an the list of MusicNotes to be removed
   * @throws IllegalArgumentException if a note is invalid or if it does not exist in the noteList
   */
  @Override
  public void removeNotes(ArrayList<MusicNote> an) {
    for (MusicNote mn : an) {
      removeNote(mn);
    }
  }

  /**
   * .
   *
   * @param n   the note in this model to be changed
   * @param p   the new pitch
   * @param oct the new octave
   * @param d   the new duration
   * @param sb  the new startBeat
   * @param v   the new volume
   * @param i   the new instrument
   * @throws IllegalArgumentException if the note is invalid or if it does not exist in the
   *                                  noteList
   */
  @Override
  public void editNote(MusicNote n, Pitch p, int oct, int d, int sb, int v, int i) {
    ArrayList<MusicNote> a = new ArrayList<MusicNote>();
    if (allNotes().contains(n)) {
      removeNote(n);
      MusicNote note = new MusicNote(p, oct, d, sb, v, i);
      addNote(note);
    } else {
      throw new IllegalArgumentException("Note Does Not Exist");
    }
  }

  /**
   * .
   *
   * @return the last beat value
   */
  public int getLastBeat() {
    int i = 0;
    for (Integer num : noteList.keySet()) {
      if (i < num) {
        i = num;
      }
    }
    return i;
  }

  /**
   * .
   *
   * @param m the model to be merged with this model
   */
  @Override
  public void mergeModels(MusicModel m) {
    ArrayList<MusicNote> a = m.allNotes();
    this.addNotes(a);
  }

  /**
   * .
   *
   * @param m the model to be played after this model
   */
  @Override
  public void addModelAtEnd(MusicModel m) {
    int i = getLastBeat() + 1;
    if (!m.allNotes().isEmpty()) {
      for (MusicNote mn : m.allNotes()) {
        mn.startBeat = mn.startBeat + i;
        addNote(mn);
      }
    }
  }

  /**
   * to get the lowest and highest pitches and octaves.
   * the first element in the list is the lowest note (has the lowest pitch and octave).
   * the second element in the list is the highest note (has the highest pitch and octave).
   *
   * @return a two element list of the lowest and highest notes
   */
  private ArrayList<MusicNote> getLowestThenHighest() {
    ArrayList<MusicNote> a = new ArrayList<MusicNote>();
    MusicNote note1 = new MusicNote(Pitch.B, 999, 4, 0, 0, 0);
    MusicNote note2 = new MusicNote(Pitch.C, -99, 4, 0, 0, 0);
    Set<Integer> noteSet = noteList.keySet();
    for (Integer num : noteSet) {
      ArrayList<MusicNote> an = noteList.get(num);
      for (MusicNote mn : an) {
        if (mn.isLowerNote(note1)) {
          note1 = mn;
        }
        if (mn.isHigherNote(note2)) {
          note2 = mn;
        }
      }
    }
    a.add(note1);
    a.add(note2);
    return a;
  }

  /**
   * .
   *
   * @return a string of all of the pitch/octave combinations from lowest to highest
   */
  public String printLowestToHighest() {
    StringBuilder acc = new StringBuilder();
    String s;
    int startPitch = getLowestThenHighest().get(0).pitch.ordinal();
    int startOctave = getLowestThenHighest().get(0).octave;
    int endPitch = getLowestThenHighest().get(1).pitch.ordinal();
    int endOctave = getLowestThenHighest().get(1).octave;
    for (int i = startOctave; i <= endOctave; i = i + 1) {
      for (int j = 0; j < 12; j = j + 1) {
        if (j == 0 && i == startOctave) {
          j = startPitch;
        }
        if (i == endOctave && j == endPitch) {
          s = Pitch.values()[j].toString() + i;
          if (s.length() == 2) {
            acc.append("  " + s + " ");
          } else if (s.length() == 3) {
            acc.append(" " + s + " ");
          } else if (s.length() == 4) {
            acc.append(" " + s);
          } else if (s.length() == 5) {
            acc.append(s);
          }
          break;
        }
        s = Pitch.values()[j].toString() + i;
        if (s.length() == 2) {
          acc.append("  " + s + " ");
        } else if (s.length() == 3) {
          acc.append(" " + s + " ");
        } else if (s.length() == 4) {
          acc.append(" " + s);
        } else if (s.length() == 5) {
          acc.append(s);
        }
      }
    }
    return acc.toString();
  }

  /**
   * to convert the string of lowest to highest notes to an ArrayList.
   * each set of five characters makes up one String element in the list.
   *
   * @return an ArrayList version of the string of lowest to highest notes
   */
  public ArrayList<String> listOfPO() {
    String s = printLowestToHighest().toString();
    ArrayList<String> a = new ArrayList<String>();
    for (int i = 0; i < s.length(); i = i + 5) {
      a.add(s.substring(i, i + 5));
    }
    return a;
  }

  /**
   * .
   *
   * @return the beatsPerMeasure value
   */
  public int getBeatsPerMeasure() {
    return beatsPerMeasure;
  }

  /**
   * .
   *
   * @return the notelist map
   */
  public HashMap<Integer, ArrayList<MusicNote>> getNotelist() {
    return noteList;
  }

  /**
   * .
   *
   * @return the tempo value
   */
  public int getTempo() {
    return tempo;
  }

  /**
   * .
   *
   * @param notes this models new notelist
   */
  public void setNoteList(ArrayList<MusicNote> notes) {
    addNotes(notes);
  }

  /**
   * .
   *
   * @param t this models new tempo
   */
  public void setTempo(int t) {
    tempo = t;
  }

  public static final class Builder implements CompositionBuilder<MusicModel> {

    MusicModel model;

    /**
     * To construct a builder object.
     */
    public Builder() {
      model = new MusicModel();
    }

    /**
     * .
     *
     * @return The new composition
     */
    @Override
    public MusicModel build() {
      return model;
    }

    /**
     * .
     *
     * @param tempo The speed, in microseconds per beat
     * @return This builder
     */
    @Override
    public CompositionBuilder setTempo(int tempo) {
      model.setTempo(tempo);
      return this;
    }

    /**
     * .
     *
     * @param start      The start time of the note, in beats
     * @param end        The end time of the note, in beats
     * @param instrument The instrument number (to be interpreted by MIDI)
     * @param pitch      The pitch (in the range [0, 127], where 60 represents C4, the middle-C on a
     *                   piano)
     * @param volume     The volume (in the range [0, 127])
     * @return this builder
     */
    @Override
    public CompositionBuilder addNote(int start, int end, int instrument, int pitch, int volume) {
      int pitchNew = pitch % 12;
      int octNew = (pitch / 12) - 1;
      int durNew = end - start;
      MusicNote note = new MusicNote(Pitch.values()[pitchNew], octNew, durNew, start, volume,
              instrument);
      model.addNote(note);
      return this;
    }
  }
}