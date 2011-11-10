package se.pulppuppets

import scala.Array._
import swing.SwingApplication

object Start extends SwingApplication{
  val videoPath = "/Users/bolliolle/Programering/scala/app/pulpVideoPlayer/target/classes/videos/"

   override def main (args:Array[String]){

    val app = new App()
    app.init(Array(videoPath+"kort4.ogg",videoPath+"part2.ogg"));
    app.run();
  }

  def startup(args: Array[String]) {}
}