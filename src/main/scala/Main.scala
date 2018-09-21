import login.{LoginCookies, LoginCredentials}
import settings.Settings._
import scalafx.Includes._
import scalafx.application
import scalafx.application.{JFXApp, Platform}
import scalafx.geometry.Insets
import scalafx.scene.Scene
import scalafx.scene.control.ButtonBar.ButtonData
import scalafx.scene.control._
import scalafx.scene.layout.{GridPane, VBox}
import scalaj.http.{Http, HttpOptions, HttpRequest}

object Main extends JFXApp {
  var sessionCookies = LoginCookies()
  stage = new application.JFXApp.PrimaryStage {
    scene = new Scene {
      title = "Battleknight Monitor"
      content = new VBox {
        val button = new Button() {
          text = "Log In"
          onAction = handle {
            if(this.text() == "Log In"){
              this.disable = true
              showLoginDialog("Log in")
              if(sessionCookies.areSet()) this.text = "Log Out"
              this.disable = false
            } else {
              this.disable = true
              logout()
              this.text = "Log In"
              this.disable = false
            }}
        }
        children = List(button)
        padding = Insets(top = 24, right = 64, bottom = 24, left = 64)
      }
    }

    def login(credentials: LoginCredentials): Boolean = {
      sessionCookies.clear()
      sessionCookies.handleUnparsed(Http(credentials.loginUrl).asString.cookies)
      if(sessionCookies.areSet()){
        true
      } else {
        false
      }
    }

    def logout(): Unit = {
      val logoutUrl = "https://s28-pl.battleknight.gameforge.com/user/logout/"
      Http(logoutUrl)
      sessionCookies.clear()
    }

    def makeNewSave() = {
      //TODO
      reloadSaves()
    }

    def reloadSaves() = {
      //TODO
    }

    def showCompareDialog(): Unit = {
      //TODO
    }

    def showLoginDialog(header: String): Unit = {
      /* Create dialog */
      val dialog = new Dialog[LoginCredentials]() {
        initOwner(stage)
        title = "Login Dialog"
        headerText = header
      }

      /* Set the button types */
      val loginButtonType = new ButtonType("Login", ButtonData.OKDone)
      dialog.dialogPane().buttonTypes = Seq(loginButtonType, ButtonType.Cancel)

      /* Create fields and labels */
      val username = new TextField() { promptText = "Username" }
      val password = new PasswordField() { promptText = "Password" }
      val server = new ComboBox[Int](for(i <- 1 to SERVER_NEWEST) yield i) { value = SERVER_DEFAULT_VALUE }

      /* Create grid and position elements */
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

      /* Enable/Disable login button depending on whether a username was entered */
      val loginButton = dialog.dialogPane().lookupButton(loginButtonType)
      loginButton.disable = true

      /* Do some validation (disable when username is empty) */
      username.text.onChange { (_, _, newValue) => loginButton.disable = newValue.trim().isEmpty}

      dialog.dialogPane().content = grid

      /* Request focus on the username field by default */
      Platform.runLater(username.requestFocus())

      /* Convert the result to LoginCredentials instance when the login button is clicked */
      dialog.resultConverter = dialogButton =>
        if (dialogButton == loginButtonType) LoginCredentials(username.text(), password.text(), server.value())
        else null

      val result = dialog.showAndWait()

      result match {
        case Some(credentials: LoginCredentials) => {
          if(!login(credentials)){
            showLoginDialog("Incorrect credentials. Try again")
          } else {
            "Correctly logged in"
          }
        }
        case None =>
      }
    }
  }
}
