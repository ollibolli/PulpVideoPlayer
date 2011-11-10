package se.pulppuppets
import org.scalatest._
import org.scalatest.BeforeAndAfter
import org.gstreamer.Gst
import java.io.File
import org.gstreamer.swing.VideoComponent

class MovieTest extends Spec with BeforeAndAfter{

  var movie:Movie = _
  val videoPath = "/Users/bolliolle/Programering/scala/app/pulpVideoPlayer/target/classes/videos/"
  var videoComponent:VideoComponent = _

  before {
    Gst.init()
    videoComponent = new VideoComponent

  }

  describe("A Movie") {
    it("should throw a WrongFileFormatException when no sutible file formate is feed to its constructor"){
        intercept[WrongFileFormatException] {
          movie = new Movie(new File(videoPath+"IMG_0103.MOV"))
      }
    }

    it("should throw FileReadException when no file or cant read"){
      intercept[FileReadException] {
        movie = new Movie(new File(videoPath+"IMG_01ddd03.MOV"))
      }
    }
    it("should be able to have a sourcefile"){
      val file=new File(videoPath+"part2.ogg")
      movie = new Movie(file)
      assert(movie.srcFile === file  )
    }

    it("should throw a NoVideoOutputException if non is set"){
      movie = new Movie(new File(videoPath+"part2.ogg"))
      intercept[NoVideoOutputException]{
        movie.play()
      }
    }

    it("should be able to get a videoOutput"){
      movie.videoOutput = videoComponent
      assert(movie.videoOutput === videoComponent)
    }

    describe(",when file succeccingly loaded, ") {

      it("should have defaultvalues"){
        assert(movie.stopTimeMillis === -1)
        assert(movie.startTimeMillis === 0)
        assert(movie.srcFile.getCanonicalPath === videoPath+"part2.ogg")
        assert(movie.rate === 1.0)

      }

      it("should play a movie out of the box"){

        movie = new Movie(new File(videoPath+"part2.ogg"))
        movie.videoOutput = videoComponent
        movie.play()
        assert(movie.isPlaying)
      }

      it("should know the duration of the movie in milliseconds"){
        movie = new Movie(new File(videoPath+"part2.ogg"))
        assert(movie.duration === 20059)
      }

      it("should be able to set a rate for the file"){
        movie = new Movie(new File(videoPath+"part2.ogg"))
        assert(movie.rate === 1.0)
        movie.rate = 2.0
        assert(movie.rate ===2.0)
      }

      it("should set starttime and throw ParameterOutOfBoundsException if starttime is smaler than 0 and grater then duration or stoptime"){
        movie = new Movie(new File(videoPath+"part2.ogg"))
        intercept[ParameterOutOfBoundsException]{
          movie.startStopTimeMillis(20060,-1)
        }
        intercept[ParameterOutOfBoundsException]{
          movie.startStopTimeMillis(-1,-1)
        }
      }

      it("throw ParameterOutOfBoundsException if stoptime is smaler than -1 and grater then duration"){
        movie = new Movie(new File(videoPath+"part2.ogg"))
        intercept[ParameterOutOfBoundsException]{
          movie.startStopTimeMillis(0,20060)
        }
        intercept[ParameterOutOfBoundsException]{
          movie.startStopTimeMillis(0,-2)
        }
      }

      it("should throw ParameterOutOfBoundsException if starttime is greater then stoptime"){
        movie = new Movie(new File(videoPath+"part2.ogg"))
        movie.stopTimeMillis = 2500
        assert(movie.stopTimeMillis === 2500)
        movie.startTimeMillis = 2499
        assert(movie.startTimeMillis === 2499)
        intercept[ParameterOutOfBoundsException]{
          movie.startTimeMillis = 2500
        }
      }

      it("should throw ParameterOutOfBoundsException if stoptime is smaler then startimetime"){
        movie = new Movie(new File(videoPath+"part2.ogg"))
        movie.startTimeMillis = 2500
        movie.stopTimeMillis = 2501
        intercept[ParameterOutOfBoundsException]{
          movie.stopTimeMillis = 2500
        }
      }

      it("shoud play with new settings"){
        movie = new Movie(new File(videoPath+"part2.ogg"))
        movie.videoOutput = videoComponent
        movie.startStopTimeMillis(2000 ,-1 )
        movie.rate = 0.5
        try {
          movie.play()
        }
        catch {
          case e => fail("Not able to start playing")
        }

        movie.startStopTimeMillis(5000 ,4000 )
        movie.rate = 1.9
        try {
          movie.play()
        }
        catch {
          case e =>fail("Not able to start playing")
        }
      }

      it("should get the time of current position"){
        movie = new Movie(new File(videoPath+"part2.ogg"))
        movie.videoOutput = videoComponent
        movie.startStopTimeMillis(2000 ,-1 )
        movie.play()
        Thread.sleep(100)
        assert(movie.time > 0 && movie.time < 100)
        Thread.sleep(100)
        assert(movie.time > 100 && movie.time < 200)
        movie.pause()
        Thread.sleep(100)
        val time = movie.time
        Thread.sleep(100)
        assert(movie.time===time)
      }
    }
  }
}