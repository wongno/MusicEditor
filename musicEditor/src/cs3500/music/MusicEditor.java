package cs3500.music;

import cs3500.music.controller.IMusicController;
import cs3500.music.controller.MusicController;
import cs3500.music.model.IMusicModel;
import cs3500.music.model.MusicModel;
import cs3500.music.model.ViewModel;
import cs3500.music.util.CompositionBuilder;
import cs3500.music.util.MusicReader;
import cs3500.music.view.IMusicView;

import java.io.FileReader;
import java.io.IOException;
import java.util.Scanner;

import javax.sound.midi.InvalidMidiDataException;

/**
 * To represent a MusicEditor.
 */
public class MusicEditor {
  /**
   * To run the code.
   */
  public static void main(String[] args) throws IOException, InvalidMidiDataException {
    // must put in both arguments correctly sequentially
    Scanner s = new Scanner(System.in);
    MusicReader reader = new MusicReader();
    CompositionBuilder<MusicModel> cb = new MusicModel.Builder();
    try {
      Readable readable1 = new FileReader(s.next());
      IMusicModel model = reader.parseFile(readable1, cb);
      IMusicController controller = new MusicController();
      controller.setModel(model);
      ViewModel viewModel = controller.createViewModel();
      IMusicView view = controller.createView(s.next());
      view.drawModel(viewModel);
    } catch (IOException e) {
      main(args);
    } catch (IllegalArgumentException a) {
      main(args);
    }
  }
}
