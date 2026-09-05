package pl.andrzejressel.monorepo.apps.shoppingmanager.application

import scalafx.application.JFXApp3
import scalafx.geometry.Pos
import scalafx.scene.Scene
import scalafx.scene.layout.VBox
import scalafx.scene.text.Text

object App extends JFXApp3 {
  override def start(): Unit =
    stage = new JFXApp3.PrimaryStage:
      title = "Shopping Manager"
      width = 800
      height = 600
      scene = new Scene {
        root = new VBox {
          alignment = Pos.Center
          spacing = 20
          children = Seq(
            new Text {
              text = "Shopping Manager"
            }
          )
        }
      }
}
