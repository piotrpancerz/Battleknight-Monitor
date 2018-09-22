import java.net.HttpCookie

import data.State
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
import scalaj.http.Http

import scala.collection.mutable

object Main extends JFXApp {
  stage = new application.JFXApp.PrimaryStage {
    var credentials: LoginCredentials = null
    var sessionCookies = LoginCookies()
    var stateList: List[State] = reloadStateList()
    scene = new Scene {
      title = "BattleKnight Monitor"
      content = new VBox {
        /* Create GUI elements */
        val logButton = new Button()
        val sliderFirst = new Slider()
        val sliderSecond = new Slider()
        val saveStateButton = new Button()
        val compareButton = new Button()
        val infoTextField = new TextField()
        val grid: GridPane = setLoginState(state = false, logButton, infoTextField, grid, sliderFirst, sliderSecond, saveStateButton, compareButton)

        /* Initialize GUI elements */
        logButton.text = "Log In"
        saveStateButton.text = "Save state"
        compareButton.text = "Compare"
        infoTextField.editable = false

        /* Initialize actions on GUI elements */
        saveStateButton.onAction = handle {
          disableElements(logButton, sliderFirst, sliderSecond, saveStateButton, compareButton, infoTextField)
          val savingResult = saveState()
          if(!savingResult._1 && savingResult._2 == "Session expired. Log in again"){
            children = setLoginState(state = false, logButton, infoTextField, grid, sliderFirst, sliderSecond, saveStateButton, compareButton)
          }
          infoTextField.text = savingResult._2
          enableElements(logButton, sliderFirst, sliderSecond, saveStateButton, compareButton, infoTextField)
        }
        logButton.onAction = handle {
          disableElements(logButton, sliderFirst, sliderSecond, saveStateButton, compareButton, infoTextField)
          if(logButton.text() == "Log In"){
            showLoginDialog("Log in")
            if(sessionCookies.areSet()) {
              children = setLoginState(state = true, logButton, infoTextField, grid, sliderFirst, sliderSecond, saveStateButton, compareButton)
              logButton.text = "Log Out"
              infoTextField.text = "Successfully logged in"
            } else {
              infoTextField.text = "Unable to log in"
            }
          } else {
            val logoutResult =  logout()
            children = setLoginState(state = false, logButton, infoTextField, grid, sliderFirst, sliderSecond, saveStateButton, compareButton)
            logButton.text = "Log In"
            infoTextField.text = logoutResult
          }
          enableElements(logButton, sliderFirst, sliderSecond, saveStateButton, compareButton, infoTextField)
        }
        compareButton.onAction = handle {
          disableElements(logButton, sliderFirst, sliderSecond, saveStateButton, compareButton, infoTextField)
          //TODO declare chosen states
//          showCompareDialog()
          enableElements(logButton, sliderFirst, sliderSecond, saveStateButton, compareButton, infoTextField)
        }

        /* Define content of VBox */
        children = grid
      }
    }

    def setLoginState(state: Boolean, logButton: Button, infoTextField: TextField, grid: GridPane, sliderFirst: Slider, sliderSecond: Slider, saveStateButton: Button, compareButton: Button): GridPane = {
      if(state){
        sliderFirst.disable = false
        sliderSecond.disable = false
        saveStateButton.disable = false
        compareButton.disable = false
        //TODO set grid layout to logged in
        new GridPane(){
          hgap = 10
          vgap = 10
          padding = Insets(20, 100, 10, 10)
          add(logButton,0,0)
          add(sliderFirst, 1, 0)
          add(sliderSecond, 0, 1)
          add(saveStateButton, 1, 1)
          add(compareButton, 0, 2)
          add(infoTextField, 1, 2)
        }
      } else {
        //TODO set grid layout to logged out
        new GridPane(){
          hgap = 10
          vgap = 10
          padding = Insets(20, 100, 10, 10)
          add(logButton,1,1)
          add(infoTextField, 1, 2)
        }
      }
    }

    def disableElements(elems: Control*): Unit = {
      elems.foreach(_.disable = true)
    }

    def enableElements(elems: Control*): Unit = {
      elems.foreach(_.disable = false)
    }

    def login(): Boolean = {
      sessionCookies.clear()
      sessionCookies.handleUnparsed(Http(credentials.loginUrl).asString.cookies)
      if(sessionCookies.areSet()){
        true
      } else {
        false
      }
    }

    def logout(): String = {
      val logoutUrl = "https://s" + credentials.server + "-pl.battleknight.gameforge.com/user/logout/"
      Http(logoutUrl)
      sessionCookies.clear()
      "Successfully logged out"
    }

    def saveState(): (Boolean, String) = {
      val response = Http("https://s" + credentials.server + "-pl.battleknight.gameforge.com/highscore/")
        .cookies(sessionCookies.cookies.map(eachCookie => new HttpCookie(eachCookie._1,eachCookie._2)).toSeq)
        .asString

      sessionCookies.handleUnparsed(response.cookies)
      if(!sessionCookies.areSet()){
        sessionCookies.clear()
        false -> "Session timed out. Log in again"
      } else {
        val offsets = List() //TODO parse response.body html, get information about how many offsets are there
        val tmpMap: mutable.Map[Int, Map[String,String]] = mutable.Map[Int, Map[String,String]]()
        offsets.foreach( eachOffset => {
          // TODO perform new request
          // TODO read data from response
          // TODO add data to tmp map
        })
        val state = State(credentials.server,tmpMap.toMap)
        //TODO serialize and save state to file
        stateList = reloadStateList()
        true -> "Successfully saved state"
      }
    }

    def reloadStateList(): List[State] = {
      // TODO get files (serialized instances of State()) from folder
      // TODO load all of them as instances of State()
      // TODO fill the list
      List()
    }

    def showCompareDialog(stateFirst: State, stateSecond: State): Unit = {
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
      val loginButtonType = new ButtonType("Log in", ButtonData.OKDone)
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
        case Some(creds: LoginCredentials) => {
          credentials = creds
          if(!login()){
            showLoginDialog("Incorrect credentials. Try again")
          }
        }
        case None =>
      }
    }
  }
}
