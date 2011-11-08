package se.pulppuppets

import java.io.File
import org.gstreamer.swing.VideoComponent
import collection.mutable.ListBuffer
import org.gstreamer.elements.{DecodeBin2, FileSrc, PlayBin2}
import org.gstreamer._
import elements.DecodeBin2
import org.gstreamer.Bus

object PulpBin{

}

/*class PulpData (name:String) extends Bin{
  val bin



  val playbin = new PlayBin2(name)

  def getElements():java.util.List[Element] = {
    return playbin.getElements
  }

  def videoSink(videosink :Element) = playbin.setVideoSink(videosink)

  def setInputFile(file: File) = {
    playbin.setInputFile(file)
  }
  def play() = {
    playbin.setState(State.PLAYING)
  }

  def paus() = {
    playbin.setState(State.PAUSED)
  }

  def state:State = {
      return playbin.getState()
  }

} */



/*
reference
http://massapi.com/source/gstreamer-java-read-only/gstreamer-java/src/org/gstreamer/example/DecodeBinPlayer.java.html*/
class PulpBin(name:String) extends Pipeline {
  final val GST_NAME: String = "pulpplaybin"

  val decodeBin = new DecodeBin2("Decode Bin");
  /*Video */
  val src = ElementFactory.make("filesrc","Source").asInstanceOf[FileSrc]
  val decodeQueue = ElementFactory.make("queue", "Decode Queue");
  addMany(src, decodeQueue, decodeBin);
  Element.linkMany(src, decodeQueue, decodeBin);

  /* Audiobin */
  val audioBin = new Bin("Audio Bin");
  val conv = ElementFactory.make("audioconvert", "Audio Convert");
  val resample = ElementFactory.make("audioresample", "Audio Resample");
  val audioSink = ElementFactory.make("autoaudiosink", "sink");

  audioBin.addMany(conv, resample, audioSink);
  Element.linkMany(conv, resample, audioSink);
  audioBin.addPad(new GhostPad("sink", conv.getStaticPad("sink")));
  add(audioBin)

  decodeBin.connect(new DecodeBin2.NEW_DECODED_PAD() {

    def newDecodedPad(elem:DecodeBin2,pad: Pad, last:Boolean) {
      /* only link once */
      if (pad.isLinked()) {
        return;
      }
      /* check media type */
      println("pad: "+ pad.toString)
      val caps = pad.getCaps();
      val struct = caps.getStructure(0);
      if (struct.getName().startsWith("audio/")) {
        println("Linking audio pad: " + struct.getName());
        pad.link(audioBin.getStaticPad("sink"));
      } else if (struct.getName().startsWith("video/")) {
        println("Linking video pad: " + struct.getName());
        val sink = getElementByName("GstVideoComponent")
        pad.link(sink.getStaticPad("sink"));
      } else {
        println("Unknown pad [" + struct.getName() + "]");
      }
    }
  });


  val bus = getBus();
  bus.connect(new Bus.ERROR() {
     def errorMessage( source: GstObject,code:Int, message:String ) {
        println("Error: code=" + code + " message=" + message);
     }
  });
  bus.connect(new Bus.INFO {
      def infoMessage(source: GstObject, code: Int, message: String) {
        println("code="+ code + " message=" + message);
      }
  })

  def inputFile(file:File){
    src.setLocation(file);
  }

  def state{
    getState()
  }

}




/*




  Parameters
  "audio-sink"               GstElement*           : Read / Write
  "frame"                    GstBuffer*            : Read
  "subtitle-font-desc"       gchar*                : Write
  "video-sink"               GstElement*           : Read / Write
  "vis-plugin"               GstElement*           : Read / Write
  "volume"                   gdouble               : Read / Write
  "connection-speed"         guint                 : Read / Write




*/