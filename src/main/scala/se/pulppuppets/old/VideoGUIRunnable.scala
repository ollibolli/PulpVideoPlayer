package se.pulppuppets.old

import swing._
import swing.Component
import swing.event.{ValueChanged, ButtonClicked}
import java.io.File
import org.gstreamer.swing.VideoComponent
import javax.swing.{WindowConstants, JFrame}
import java.awt.{AlphaComposite, Graphics, BorderLayout}
import collection.mutable.Buffer
import org.gstreamer.{Element, GstObject, Bus, State}

/*object VideoGUIRunnable {
  def apply(movie:File) = new VideoGUIRunnable(movie)
}


class VideoGUIRunnable(movie:File) extends Runnable{
  var inc = 50
  var playBin = new MovieBuffer("player a")
  try {
    println(movie.getAbsolutePath)
    movie.canRead
    playBin.setInputFile(movie)
  }
  catch {
    case e:Exception => e.printStackTrace()
  }

  def run() {

    val frame = new JFrame("Video Player");
    frame.setPreferredSize(new Dimension(640, 502));
    frame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
    try {
      val videoComponentA = new TransparentVideoComponent(1.0f);
      //val videoComponentA = new VideoComponent();
      //playBin.setVideoSink(videoComponentA.getElement());
      frame.getContentPane().add(BorderLayout.CENTER, videoComponentA);
      playBin.play()
    }
    catch {
      case e :Exception => e.printStackTrace()
    }
    frame.pack();
    frame.setVisible(true);


  }
  def stop(){
    playBin.stop();
  }
}
*/

class TransparentVideoComponent(alpha :Float) extends VideoComponent {
  var alfa = alpha
  override def paint(g:Graphics ) {
    val g2 = g.asInstanceOf[Graphics2D]
    g2.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, alfa));
    println(alfa)
    super.paint(g2);
    g2.dispose();
  }

}

class VideoGUIRunnable2(movie:Buffer[File]) extends Runnable with Player with Reactor{
  var videoComponent: TransparentVideoComponent = null;
  var playState = true
  var index = 0;
  //  var playBin = new MovieBuffer("firstMovie", movie(0), videoComponent)
  assert(movie(index).canRead)
  var playBin = new PulpBin("firstMovie")
  playBin.inputFile(movie(index))
  listenTo(XSlider,PlayPausButton,NextButton,PreviousButton)
  reactions += {
    case v :ValueChanged => {
      videoComponent.alfa = XSlider.value.floatValue() / XSlider.max.floatValue()
      println(videoComponent.alfa)
    }
    case ButtonClicked(`NextButton`) => {
       next()
    }
    case ButtonClicked(`PlayPausButton`) => {
       playPaus()
    }
    case ButtonClicked(`PreviousButton`) => {
       previous()
    }

  }

  def run() {
    videoComponent = new TransparentVideoComponent(1.0f)
    videoComponent.setMinimumSize(new Dimension(640,480))
    val frame = new Frame{
      title = "VideoPlayer"
      contents = new BoxPanel(Orientation.Horizontal){
        contents += Component.wrap(videoComponent)
      }
      visible = true
    }
    playBin.add(videoComponent.getElement)
    println("Videoelement" + videoComponent.getElement.getName())
    do {
    if (playState){playBin.play()}
    else {playBin.pause()}
    } while (playBin.getState == State.NULL)
    playBin.play()
    frame.size = new Dimension(640,502)
  }

  def loadAndStartMovie {
    println("index = " + index)
    assert(movie(index).canRead)
    playBin.inputFile(movie(index))
    println(playBin.state)

    while (playBin.getState != State.PLAYING) {
      println(playBin.state)
      playBin.play()
    }
  }

  def next() = {
    if (index < movie.length-1 ){
      index +=1
      loadAndStartMovie
    } else {
      println("DAGS ATT SLÄCKA NEXT")
    }
    println("movie =" + movie(index).getName)
  }

  def playPaus() = {
    playState = !playState
    if (playState){
      playBin.pause()
      PlayPausButton.text="[ Play ]"
    } else {
      playBin.play()
      PlayPausButton.text="[ Stop ]"
    }
  }

  def previous() = {
    if (index > 0){
      index -= 1
      loadAndStartMovie
      println("movie =" + movie(index).getName)
    } else {
      println("DAGS ATT SLÄCKA PREV")
    }
  }

}
