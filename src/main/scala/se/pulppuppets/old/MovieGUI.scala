package se.pulppuppets.old

import swing.{ListView, Table, ScrollPane, Frame}

/**
 * Created by IntelliJ IDEA.
 * User: bolliolle
 * Date: 2011-08-29
 * Time: 11.38
 * To change this template use File | Settings | File Templates.
 */

object MovieGUI extends Runnable{

  def run() {
    val frame = new Frame{
      title = "Movie Management"
      contents = new Spreadsheet(400,30) {

      }

    }
    frame.visible = true
  }

  class Spreadsheet(val height: Int, val width: Int)
    extends ScrollPane
  {

    val table = new Table(height, width) {
      rowHeight = 25
      autoResizeMode = Table.AutoResizeMode.Off
      showGrid = true
      gridColor = new java.awt.Color(150, 150, 150)
    }
    table.update(1,1,"kompis")
    val rowHeader =
      new ListView((0 until height) map (_.toString)) {
        fixedCellWidth = 30
        fixedCellHeight = table.rowHeight
      }

    viewportView = table
    rowHeaderView = rowHeader
  }


}