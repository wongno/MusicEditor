package cs3500.music.view;

import java.util.ArrayList;

import cs3500.music.model.IMusicModel;
import cs3500.music.model.MusicNote;
import cs3500.music.model.ViewModel;

/**
 * To represent the console view for the MusicEditor.
 */
public class ConsoleViewImpl implements IMusicView {

  Appendable ap;

  /**
   * To construct a music view object.
   */
  public ConsoleViewImpl() {
    // empty constructor
  }

  /**
   * does a note (based on the pitch/octave combination) exist at the given int.
   *
   * @param s the pitch/octave combination
   * @param i the beat the note could be hashed to
   * @return true if a note exists at the given string and false if the note does not
   */
  private boolean existingNote(IMusicModel mm, String s, int i) {
    ArrayList<MusicNote> a = new ArrayList<MusicNote>();
    ArrayList<MusicNote> all = mm.allNotes();
    for (MusicNote m : all) {
      if (s.contains(m.combinePitchOctave())) {
        a.add(m);
      }
    }
    for (MusicNote m : a) {
      if (i >= m.getStartBeat() && i < m.getStartBeat() + m.getDuration()) {
        return true;
      }
    }
    return false;
  }

  /**
   * does the note that exists at the given string start at the given int.
   *
   * @param s the pitch/octave combination
   * @param i the beat the note could start at
   * @return true if the note starts at the int and false if it does not
   */
  private boolean isStartBeat(IMusicModel mm, String s, int i) {
    for (MusicNote m : mm.allNotes()) {
      if (s.contains(m.combinePitchOctave())) {
        if (i == m.getStartBeat()) {
          return true;
        }
      }
    }
    return false;
  }

  /**
   * contains the pitch/octave combination, the number of beats, the start
   * of a note represented as an "X", the rest of a note represented
   * with a "|", and empty spaces where a note does not exist.
   *
   * @param model the given model to be made into one of three views.
   */
  @Override
  public void drawModel(ViewModel model) {
    StringBuilder acc = new StringBuilder("");
    if (this == null || model.allNotes().isEmpty()) {
      acc.append("No beat(s) to drop");
    } else {
      for (int spaceNum = 0; spaceNum < Integer.toString(model.getLastBeat()).length();
           spaceNum = spaceNum + 1) {
        acc.append(" ");
      }
      acc.append(model.printLowestToHighest() + "\n");
      for (int i = 0; i <= model.getLastBeat(); i = i + 1) {
        acc.append(String.format("%" + Integer.toString(model.getLastBeat()).length() + "s",
                Integer.toString(i)));
        for (String s : model.listOfPO()) {
          if (existingNote(model, s, i)) {
            if (isStartBeat(model, s, i)) {
              acc.append("  X  ");
            } else {
              acc.append("  |  ");
            }
          } else {
            acc.append("     ");
          }
        }
        acc.append("\n");
      }
    }
    ap = acc;
    System.out.println(ap);
  }

  /**
   * .
   *
   * @throws IllegalArgumentException if this method is called for the console view
   */
  @Override
  public void jumpHome() {
    throw new IllegalArgumentException("Not For This Class.");
  }

  /**
   * .
   *
   * @throws IllegalArgumentException if this method is called for the console view
   */
  @Override
  public void jumpEnd() {
    throw new IllegalArgumentException("Not For This Class.");
  }
}
