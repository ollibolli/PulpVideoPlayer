package se.pulppuppets

import java.io.File
import org.gstreamer.swing.VideoComponent
import collection.mutable.ListBuffer
import org.gstreamer.elements.{DecodeBin2, FileSrc, PlayBin2}
import org.gstreamer._
import elements.DecodeBin2.REMOVED_DECODED_PAD
import event.SeekEvent
import org.gstreamer.Bus._
import java.util.concurrent.TimeUnit

object PulpBin{
}
/*
reference
http://massapi.com/source/gstreamer-java-read-only/gstreamer-java/src/org/gstreamer/example/DecodeBinPlayer.java.html*/
class PulpBin(name:String) extends PlayBin2(name) { //Pipeline
  final val GST_NAME: String = "pulpBin"

  var rate : Double = 1.0
  var startTime:Long = null.asInstanceOf[Long]
  var stopTime:Long = null.asInstanceOf[Long]
  var sourcePath:String =""
  var alpha:Double = null.asInstanceOf[Double]

  private var fps : Double = this.getVideoSinkFrameRate

  private def implSeek(rate:Double) {
    do {
      this.pause()
      Thread.sleep(5)
    } while (this.getState != State.PAUSED)

    var fps = this.fps
    val ifps:Double = 30
    val f:Double = { if (0 < ifps && 0 < fps) ifps / fps else 1}
    val timePossition:Long = this.queryPosition(TimeUnit.NANOSECONDS);
    val timeDuration:Long = this.queryDuration(TimeUnit.NANOSECONDS);
    printf("Pulpbin impSeek %d" ,timeDuration)
    var res:Boolean = false
    var start : Long = null.asInstanceOf[Long]
    var stop :Long = null.asInstanceOf[Long]
    if (rate > 0) {
      start = timePossition;
      stop = -1;
    } else {
      start = 0;
      stop = timePossition;
    }
    res = this.seek(rate * f, Format.TIME, SeekFlags.FLUSH, SeekType.SET, start, SeekType.SET, stop);

    if (!res) {
      throw new RuntimeException("Change rate faild")
    }
    this.fps=ifps
  }

  override def play() = {
     implSeek(this.rate)
     super.play();
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