package login

import java.math.BigInteger
import settings.Settings._
import java.security.MessageDigest

case class LoginCredentials(username: String, password: String, server: Int = SERVER_DEFAULT_VALUE){
  val parsedUsername: String = username.replace(" ","%20")
  val encryptedPassword: String = {
    val bytes = MessageDigest.getInstance("MD5").digest(password.getBytes())
    val bigInt = new BigInteger(1, bytes)
    bigInt.toString(16)
  }
  val loginUrl = "https://s" + server.toString + "-pl.battleknight.gameforge.com/main/login/" + parsedUsername + "/" + encryptedPassword + "?kid=&servername=null&serverlanguage=null"
}
