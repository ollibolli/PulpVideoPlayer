package se.pulppuppets.old

import org.gstreamer.Gst
import java.io.File
import javax.swing.SwingUtilities
import collection.mutable.{Buffer}
import org.gstreamer.elements.{PlayBin2}

/**
 * Hello world!
 *
 */
object App
{
  val videoPath = "/Users/bolliolle/Programering/scala/app/pulpVideoPlayer/target/classes/videos/"
  def main(args:Array[String]){
    println("Starting all")
    Gst.init("Video",args)
    var movies = Buffer(new File(videoPath+"part2.ogg"),new File(videoPath+"kort4.ogg"),new File(videoPath+"part2.ogg"),new File(videoPath+"kort4.ogg"))

    SwingUtilities.invokeLater(new VideoGUIRunnable2(movies))
    SwingUtilities.invokeLater(ControllerGUI)
    SwingUtilities.invokeLater(MovieGUI)
    Gst.main()
  }

}

class App {


}

