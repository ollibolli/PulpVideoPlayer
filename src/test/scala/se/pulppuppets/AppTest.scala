package se.pulppuppets;


import org.scalatest.junit.JUnitSuite
import org.junit.Before
import org.junit.Test
import collection.mutable.{ArrayBuffer, Buffer}
import org.gstreamer.Bin

class AppTest extends JUnitSuite {

  val app = new App()
  val bin = new PulpBin()

  @Before def init():Unit = {

  }
  @Test def testInit : Unit = {
    assert(app.playbins.isInstanceOf[ArrayBuffer[PulpBin]])
    assert(app.playbins.length==0)

    app.playbins.foreach(pulp => {
      assert(pulp.isInstanceOf[PulpBin])
    })
  }

}

