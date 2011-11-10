package se.pulppuppets.old

import swing._

case object ControllerGUI extends Runnable {

  def run() {
    val controllerFrame = new Frame {
      title = "kompis"
      contents = new FlowPanel {
        contents += XSlider
        contents += PreviousButton
        contents += PlayPausButton
        contents += NextButton
      }
      visible = true
    }

  }


}

object XSlider extends Slider{
  max = 100
  min = 0
  value = 50
}

object NextButton extends Button {
  text = "Next >"
}

object PlayPausButton extends Button {
  text = "[ Play ]"
}

object PreviousButton extends Button {
  text = "< Prev"
}
