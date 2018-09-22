package login

import java.net.HttpCookie

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

  def handleUnparsed(cookies: IndexedSeq[HttpCookie]): Unit = {
    cookies.foreach({ eachCookie => {
      val name = eachCookie.getName()
      val value = eachCookie.getValue()
      if(keys.contains(name)) this(name) = value
    }})
  }

  def areSet(): Boolean = {
    cookies.foreach( eachCookie => {
      if(eachCookie._2 == "" || eachCookie._2 == "deleted") return false
    })
    true
  }

  def clear(): Unit = {
    keys.foreach( key => cookies(key) = "")
  }
}
