package login

import scala.collection.mutable

case class LoginCookies(){
  val keys = Set("BattleKnight","BattleKnightSession")
  val cookies: mutable.Map[String,String] = {
    val immutableMap = keys.zip(Array.fill(keys.size)("")).toMap
    mutable.Map() ++ immutableMap
  }

  def apply(): mutable.Map[String,String] = cookies

  def apply(name: String): String = if(keys.contains(name)) cookies(name) else ""

  def update(name: String, value: String): Unit = if(keys.contains(name)) cookies(name) = value

  def handleUnparsed(strings: Vector[String]): Unit = {
    strings.foreach({ eachCookie => {
      eachCookie.split("=") match {
        case Array(name: String, value: String, _*) => if(keys.contains(name)) this(name) = value
        case _ =>
      }
    }})
  }

  def areSet(): Boolean = {
    cookies.foreach( eachCookie => {
      if(eachCookie._2 == "") return false
    })
    true
  }

  def clear(): Unit = {
    keys.foreach( key => cookies(key) = "")
  }
}
