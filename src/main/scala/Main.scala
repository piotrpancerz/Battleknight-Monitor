import login.LoginCredentials
import settings.Settings._
import scalafx.Includes._
import scalafx.application
import scalafx.application.{JFXApp, Platform}
import scalafx.event.ActionEvent
import scalafx.geometry.Insets
import scalafx.scene.Scene
import scalafx.scene.control.ButtonBar.ButtonData
import scalafx.scene.control._
import scalafx.scene.layout.{GridPane, VBox}

object Main extends JFXApp {
  val loggedIn = false
  stage = new application.JFXApp.PrimaryStage {
    title = "Battleknight Monitor"
    scene = new Scene {
      title = ""
      content = new VBox {
        children = new Button(if(!loggedIn) "Login" else "Logout") {
          onAction = handle {if(!loggedIn) showLoginDialog() else logout()}
        }
        padding = Insets(top = 24, right = 64, bottom = 24, left = 64)
      }
    }

    def login(): Unit = {
      //TODO
    }

    def logout(): Unit = {
      //TODO
    }

    def showMainDialog(): Unit = {
      //TODO
    }

    def showLoginDialog(): Unit = {

      // Create the custom dialog.
      val dialog = new Dialog[LoginCredentials]() {
        initOwner(stage)
        title = "Login Dialog"
        headerText = "Please, log in"
        //      graphic = new ImageView(this.getClass.getResource("login_icon.png").toString)
      }

      // Set the button types.
      val loginButtonType = new ButtonType("Login", ButtonData.OKDone)
      dialog.dialogPane().buttonTypes = Seq(loginButtonType, ButtonType.Cancel)

      /* Create fields and labels */
      val username = new TextField() { promptText = "Username" }
      val password = new PasswordField() { promptText = "Password" }
      val server = new ComboBox[Int](for(i <- 1 to SERVER_NEWEST) yield i) { value = SERVER_DEFAULT_VALUE }

      val grid = new GridPane() {
        hgap = 10
        vgap = 10
        padding = Insets(20, 100, 10, 10)

        add(new Label("Username:"), 0, 0)
        add(username, 1, 0)
        add(new Label("Password:"), 0, 1)
        add(password, 1, 1)
        add(new Label("Server:"), 0, 2)
        add(server, 1, 2)
      }

      // Enable/Disable login button depending on whether a username was entered.
      val loginButton = dialog.dialogPane().lookupButton(loginButtonType)
      loginButton.disable = true

      // Do some validation (disable when username is empty).
      username.text.onChange { (_, _, newValue) => loginButton.disable = newValue.trim().isEmpty}

      dialog.dialogPane().content = grid

      // Request focus on the username field by default.
      Platform.runLater(username.requestFocus())

      // Convert the result to a username-password-pair when the login button is clicked.
      dialog.resultConverter = dialogButton =>
        if (dialogButton == loginButtonType) LoginCredentials(username.text(), password.text(), server.value())
        else null

      val result = dialog.showAndWait()

      result match {
        case Some(LoginCredentials(u, p, s)) => println("Username=" + u + ", Password=" + p)
        case None               => println("Dialog returned: None")
      }
    }
  }



  def login(credentials: LoginCredentials): Boolean = {
    //TODO perform login, return true if logged in, false if not
    ???
  }

  def setPosition(item: Control, x: Int, y: Int): Unit = {
    item.layoutX = x
    item.layoutY = y
  }
}
