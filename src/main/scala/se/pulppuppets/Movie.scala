package se.pulppuppets

import org.gstreamer.elements.PlayBin2
import java.io.File
import java.awt.Component
import org.gstreamer.swing.VideoComponent
import java.util.concurrent.TimeUnit
import scala.actors._
import scala.actors.Actor._
import org.gstreamer.Bus.{STATE_CHANGED, MESSAGE, SEGMENT_START, SEGMENT_DONE}
import org.gstreamer.{SeekType, SeekFlags, Format, GstObject, Bus, State => GSState}

/**
 * A scala wrapper for gstreamer-java Playbin2
 */
class Movie(file:File){
  private var _playBin:PlayBin2 = _
  private var _srcFile :File = _
  private var _videoOutput:VideoComponent = _
  private var _rate:Double = 1.0
  private var _fps : Double = _
  private var _startTime:Long = 0
  private var _stopTime:Long = -1
  private var _bus : Bus= _

  var duration:Long = _

  if (file.isFile && file.canRead){
    if (file.getName.endsWith(".ogg") || file.getName.endsWith(".OGG")){
      val playBin = new PlayBin2(file.getName)
      playBin.setInputFile(file)
      do {
        playBin.pause()
        Thread.sleep(5)
      } while (playBin.getState != GSState.PAUSED)
      _playBin = playBin
      _srcFile = file
      _fps = playBin.getVideoSinkFrameRate
      duration = playBin.queryDuration(TimeUnit.MILLISECONDS)
      _bus = playBin.getBus
    } else throw new WrongFileFormatException("Expected ogg format")
  }else throw new FileReadException("File don't exist or no permission to read")

  _bus.connect(new Bus.EOS() {
     def endOfStream(source:GstObject) {
       println("END OF STREAM ")
     }
   })

  _bus.connect(new Bus.SEGMENT_START {
    def segmentStart(source: GstObject, format: Format, position: Long) {
      try {
        val movie = source.asInstanceOf[PlayBin2]
        println("start")
        println(movie.queryDuration.toMillis)
      }
    }
  })

  _bus.connect(new Bus.STATE_CHANGED {
    def stateChanged(source: GstObject, old: GSState, current: GSState, pending: GSState) {
        try {
          val movie = source.asInstanceOf[PlayBin2]
          println(movie.queryPosition.toMillis)
        }
    }
  })

  private def init() {
    _playBin.pause()
  }

  def srcFile_= (file:File) {
    println(_srcFile)
  }

  def srcFile:File = {
    _srcFile
  }

  def play() {
    implSeek()
    if (_videoOutput.isInstanceOf[VideoComponent]) {
      do {
        _playBin.play()
      } while (_playBin.getState != GSState.PLAYING)
    } else throw new NoVideoOutputException("No org.gstreamer.swing.VideoComponent is set for VideoOutput")
  }

  def videoOutput_= (component :VideoComponent) {
    _playBin.setVideoSink(component.getElement)
    _videoOutput = component;
  }

  def videoOutput:VideoComponent = {
    _videoOutput
  }

  def isPlaying:Boolean = {
    _playBin.isPlaying
  }

  def rate_=(rate:Double) {
    if (-10 < rate && rate < 10){
      _rate = rate
    } else throw new ParameterOutOfBoundsException("Expect bounds between -10 to 10 but got "+ rate )
  }

  def rate :Double = {
    _rate
  }

  def pause(){
    do {
      _playBin.pause()
    } while (_playBin.getState != GSState.PAUSED)
  }


  /**
   * Set the start and end possition of the movie
   * @param startMilliseconds Long
   * @throws ParameterOutOfBoundsException
   */
  def startStopTimeMillis(startMilliseconds: Long, stopMilliseconds:Long) {
    if (startTimeMillis < stopMilliseconds || stopMilliseconds == -1){
      if (startMilliseconds > -1 && startMilliseconds <= duration){
        _startTime = startMilliseconds
      } else throw new ParameterOutOfBoundsException("Expected value between 0 and "+duration+" got "+ startMilliseconds)

      if (stopMilliseconds >= -1 && stopMilliseconds <= duration){
        _stopTime = stopMilliseconds
      } else throw new ParameterOutOfBoundsException("Expected value between -1 and "+duration+" got "+ stopMilliseconds)
    } else throw new ParameterOutOfBoundsException("Expected value stopTimeMills grater then startTimeMillis")
  }

  /**
   * Set the start possition of the movie
   * @param milliseconds Long
   * @throws ParameterOutOfBoundsException
   */
  def startTimeMillis_=(milliseconds: Long) {
    if (milliseconds > -1 && milliseconds <= duration){
      if (milliseconds < stopTimeMillis || stopTimeMillis == -1) {
        _startTime = milliseconds
      } else throw new ParameterOutOfBoundsException("Expected value less then stopTimeMillis")
    } else throw new ParameterOutOfBoundsException("Expected value between 0 and "+duration+" got "+ milliseconds)
  }

  def startTimeMillis :Long = {
    _startTime
  }

  /**
   * Set the end possition of the movie
   * @param milliseconds Long if set to -1 movie play to the end
   * @throws ParameterOutOfBoundsException
   */
  def stopTimeMillis_=(milliseconds: Long) {
    if (milliseconds >= -1 && milliseconds <= duration){
      if (milliseconds > startTimeMillis) {
        _stopTime = milliseconds
      } else throw new ParameterOutOfBoundsException("Expected value grater then startTimeMillis")
    } else throw new ParameterOutOfBoundsException("Expected value between -1 and "+duration+" got "+ milliseconds)
  }

  def stopTimeMillis:Long = {
    _stopTime
  }

  private def implSeek() {
    //   val ifps:Double = 30
    //    val f:Double = { if (0 < ifps && 0 < _fps) ifps / _fps else 1}
    val timePossition:Long = _playBin.queryPosition(TimeUnit.NANOSECONDS);
    var res:Boolean = false

    var startNanoseconds : Long = null.asInstanceOf[Long]
    var stopNanoseconds :Long = null.asInstanceOf[Long]

    do {
      pause()
    } while (_playBin.getState != GSState.PAUSED)

    if (rate > 0) {
      startNanoseconds = timePossition;
      stopNanoseconds = { if (stopTimeMillis== -1) -1 else stopTimeMillis * 1000000};
    } else {
      startNanoseconds = startTimeMillis * 1000000;
      stopNanoseconds = timePossition;
    }
    res = _playBin.seek(rate /* * f*/, Format.TIME, SeekFlags.FLUSH, SeekType.SET, startNanoseconds, SeekType.SET, stopNanoseconds);

    if (!res) {
      throw new RuntimeException("Change rate faild")
    }
  }

  var i = 0
  def time :Long = {
    i = i+1
    _playBin.queryPosition().toMillis
  }

}
