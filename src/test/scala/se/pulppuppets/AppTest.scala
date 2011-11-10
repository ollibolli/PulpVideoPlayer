package se.pulppuppets;

import org.scalatest.junit.JUnitSuite
import org.junit.Assert._
import org.junit.Before
import org.junit.Test
import collection.mutable.{ArrayBuffer, Buffer}
import org.gstreamer.{Gst, Bin}

class AppTest extends JUnitSuite {
  val videoPath = "/Users/bolliolle/Programering/scala/app/pulpVideoPlayer/target/classes/videos/"

  var app :App= null
  var bin :MovieBuffer= null

  @Before def init()  {
     app = new App()
     bin = new MovieBuffer()
  }
  @Test def testInit() {
    app.init(Array(videoPath+"kort4.ogg",videoPath+"part2.ogg"));
    expect(2)(app.pulpData.length);
    assertTrue("Gst inittalized", Gst.isInitialized)
  }

  @Test def testRun() {
    //App has to be initialized before the run method otherwise expec expeception
    try {
      app.run()
      assert(false)
    } catch {
      case e:Exception => assert(true)
    }
    assertFalse(app.initialized)
    app.init(Array(videoPath+"kort4.ogg",videoPath+"part2.ogg"))
    println(app.initialized)
    assertTrue(app.initialized)
    try {
      app.run()
      assert(true)
    } catch {
      case e:Exception => assert(false);e.printStackTrace()
    }

  }


}

