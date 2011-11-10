package se.pulppuppets

import old.{MovieGUI, ControllerGUI, VideoGUIRunnable2}
import scala.collection.mutable.ArrayBuffer
import java.io.File
import org.gstreamer.Gst
import javax.swing.SwingUtilities

class App extends Runnable{

  var pulpData:MovieBuffer = new MovieBuffer
  var initialized:Boolean = false

  def init() {
    initialized = true;
    Gst.init()
  }

  def init(movies : Array[String]) {
    this.init()
    try {
      movies.foreach((movie) => {
        if (movie.endsWith(".ogg") || movie.endsWith(".OGG")) {
          //TODO make a regexp to filter the filename and put it as a identifier as PulpBin argument
          var pulpbin = new PulpBin("movie" + movies.indexOf(movie))
          pulpbin.setInputFile(new File(movie))
          pulpbin.rate=0.3
          println(pulpbin.rate)
          pulpData += pulpbin
        }
      })
    } catch {
      case e:Exception => {e.printStackTrace() }

    }
  }

  def run() {
    if (!initialized) throw new Exception("App is Not Initizialized")
    SwingUtilities.invokeLater(new VideoOut(pulpData))
    SwingUtilities.invokeLater(GUI)
  }
}
