package login

import java.math.BigInteger
import settings.Settings._
import java.security.MessageDigest

case class LoginCredentials(private val _username: String, private val _password: String, private var _server: Int = SERVER_DEFAULT_VALUE){
  if (_server < 1 || _server > SERVER_NEWEST) _server = SERVER_DEFAULT_VALUE

  def server: Int = _server
  def parsedUsername: String = _username.replace(" ","%20")
  def encryptedPassword: String = {
    val bytes = MessageDigest.getInstance("MD5").digest(_password.getBytes())
    val bigInt = new BigInteger(1, bytes)
    bigInt.toString(16)
  }
  def loginUrl = "https://s" + _server.toString + "-pl.battleknight.gameforge.com/main/login/" + parsedUsername + "/" + encryptedPassword + "?kid=&servername=null&serverlanguage=null"
}
