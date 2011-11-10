package se.pulppuppets

import swing._
import event.{KeyReleased, ButtonClicked, ValueChanged}
import org.gstreamer.swing.VideoComponent
import org.gstreamer.elements.PlayBin2
import org.gstreamer.{SeekFlags, SeekType, Format, State}
import javax.swing.JFrame
import java.awt.{DisplayMode, Toolkit, BorderLayout, Color, Window => AWTWindow, AlphaComposite, Graphics, GraphicsEnvironment, GraphicsDevice}

class VideoOut(pulpData:MovieBuffer) extends Runnable with Reactor {
  private var videoComponent: VideoComponent = null
  private var playState = true
  private var index=0
  private val fullscreenJFrame = new FullscreenWindow

  if (pulpData.isEmpty) throw new Exception("No Data is feed")

  var playbin = pulpData(index)
  listenTo(XSlider,PlayPausButton,NextButton,PreviousButton)
  reactions += {
    /*case v :ValueChanged => {
      videoComponent.alfa = XSlider.value.floatValue() / XSlider.max.floatValue()
      println(videoComponent.alfa)
    } */
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
    println("VIDEO out RUN")
    videoComponent = new VideoComponent();
    playbin.setVideoSink(videoComponent.getElement);
    fullscreenJFrame.getContentPane.add(videoComponent,BorderLayout.CENTER);
    // Wait until a video pad is linked to make it visible
    try {
      showOnScreen(1,fullscreenJFrame)
    }catch {
      case e:Exception => {e.printStackTrace()}
    }
    fullscreenJFrame.setVisible(true)
    playbin.play();
  }


  def next() = {
    if (index < pulpData.length-1 ){
      println("pulpDataIndex ", pulpData(index))
      replaceAndPlay(pulpData(index),pulpData(index+1))
      index += 1
    } else {
      println("DAGS ATT SLÄCKA NEXT")
    }
    println("movie =" + pulpData(index).getName)
  }

  def playPaus() = {
    playState = !playState
    if (playState){
      pulpData(index).pause()
      PlayPausButton.text="[ Play ]"
      println("[ Play ]")
    } else {
      pulpData(index).play()
      PlayPausButton.text="[ Stop ]"
      println("[ Stop ]")
    }
  }

  def previous() = {
    if (index > 0){
      pulpData(index).stop()
      replaceAndPlay(pulpData(index),pulpData(index-1));
      println("movie =" + pulpData(index).getName)
      index -= 1
    } else {
      println("DAGS ATT SLÄCKA PREV")
    }
  }


  private def replaceAndPlay(oldBin: PlayBin2,newBin :PlayBin2) {
    if (oldBin.isPlaying ){
      oldBin.pause()
    }
    oldBin.stop()
    newBin.setVideoSink(videoComponent.getElement)
    newBin.play();
    println(newBin.getState);
  }

  private def showOnScreen(screen: Int, frame :JFrame)
  {
    val ge:GraphicsEnvironment = GraphicsEnvironment.getLocalGraphicsEnvironment;
    val gs = ge.getScreenDevices;
    if( screen > -1 && screen < gs.length ){
      for(gconf <- gs(screen).getConfigurations){
        val bounds = gconf.getBounds
        frame.setBounds(bounds.getBounds)
      }
    } else {
        gs(0).setFullScreenWindow(frame)
    }
  }

  class FullscreenWindow extends JFrame {
    this.setBackground(Color.BLUE)
    this.setResizable(false);
    if (!this.isDisplayable) this.setUndecorated(true);
    val screenSize = Toolkit.getDefaultToolkit.getScreenSize;
    setBounds(0,0,screenSize.width, screenSize.height);  }

}

/*class TransparentVideoComponent(pAlpha :Float) extends VideoComponent {
  private var alfa= pAlpha

  override def paint(g:Graphics ) {
    val g2 = g.asInstanceOf[Graphics2D]
    g2.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, alfa));
    super.paint(g2);
    g2.dispose();
  }

  def alpha_= (pAlpha:Float) {
    alfa = pAlpha
  }

  def alpha() :Float = {
    alfa
  }
} */

