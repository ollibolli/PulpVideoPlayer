package se.pulppuppets

import org.junit.{Before, Test}
import org.junit.Assert._
import org.gstreamer.Gst

/**
 * Created by IntelliJ IDEA.
 * User: bolliolle
 * Date: 2011-09-21
 * Time: 23.01
 * To change this template use File | Settings | File Templates.
 */

class PulpDataTest {
  var pD :MovieBuffer = null

  @Before def init{
    Gst.init
    pD = new MovieBuffer
  }

  @Test def test_+=(){
    assertEquals(0,pD.size)
    pD.+=(new PulpBin("TEST1"))
    assertEquals(1,pD.size)
  }
}