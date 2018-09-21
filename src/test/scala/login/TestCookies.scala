package login

import java.net.HttpCookie

import org.junit.Assert._
import org.junit._

class TestCookies {
  private var cookies: LoginCookies = _

  @Before
  def createLoginCookieInstance = cookies = LoginCookies()

  @Test
  def keysLength = {
    assertEquals(2, cookies.keys.size)
  }

  @Test
  def cookiesType = {
    assertTrue(cookies.cookies.isInstanceOf[scala.collection.mutable.Map[String,String]])
  }

  @Test
  def cookiesLengthOnCreate = {
    assertEquals(2, cookies.cookies.size)
  }

  @Test
  def cookiesKeysOnCreate = {
      assertEquals(cookies.cookies.keys, cookies.keys)
  }

  @Test
  def cookiesValuesOnCreate = {
    cookies.cookies.foreach( eachTuple => {
      assertTrue(cookies.keys.contains(eachTuple._1))
      assertEquals("",cookies(eachTuple._2))
    })
  }

  @Test
  def applyMethodWithNoArguments = {
    assertEquals(cookies(), cookies.cookies)
  }

  @Test
  def applyMethodWithFirstProperStringArgument = {
    assertEquals("", cookies("BattleKnight"))
  }

  @Test
  def applyMethodWithSecondProperStringArgument = {
    assertEquals("", cookies("BattleKnightSession"))
  }

  @Test
  def applyMethodWithOneImproperStringArgument = {
    assertEquals("", cookies("ImproperArgument"))
  }

  @Test
  def updateMethodWithProperArguments = {
    cookies("BattleKnight") = "test1"
    cookies("BattleKnightSession") = "test2"
    assertEquals("test1", cookies("BattleKnight"))
    assertEquals("test2", cookies("BattleKnightSession"))
    assertEquals(2, cookies().size)
  }

  @Test
  def updateMethodWithImproperArguments = {
    cookies("BattleKnight") = "test1"
    cookies("BattleKnightSession") = "test2"
    cookies("ImproperArgument") = "test3"
    assertEquals("test1", cookies("BattleKnight"))
    assertEquals("test2", cookies("BattleKnightSession"))
    assertEquals("", cookies("ImproperArgument"))
    assertEquals(2, cookies().size)
  }

  @Test
  def handleUnparsed = {
    val seq = IndexedSeq(new HttpCookie("yyy","test1"), new HttpCookie("BattleKnightSession","test2"), new HttpCookie("xxx","test3"), new HttpCookie("BattleKnight","test4"))
    cookies.handleUnparsed(seq)
    assertEquals("", cookies("yyy"))
    assertEquals("test2", cookies("BattleKnightSession"))
    assertEquals("", cookies("xxx"))
    assertEquals("test4", cookies("BattleKnight"))
    assertEquals(2, cookies().size)
  }

  @Test
  def handleUnparsedDuplicatedKeys = {
    val seq = IndexedSeq(new HttpCookie("BattleKnight","test1"), new HttpCookie("BattleKnightSession","test2"), new HttpCookie("BattleKnightSession","test3"), new HttpCookie("BattleKnight","test4"))
    cookies.handleUnparsed(seq)
    assertEquals("test3", cookies("BattleKnightSession"))
    assertEquals("test4", cookies("BattleKnight"))
    assertEquals(2, cookies().size)
  }

  @Test
  def areSetProper = {
    cookies("BattleKnight") = "test1"
    cookies("BattleKnightSession") = "test2"
    assertTrue(cookies.areSet())
  }

  @Test
  def areSetImproper1 = {
    cookies("BattleKnight") = ""
    cookies("BattleKnightSession") = "test2"
    assertFalse(cookies.areSet())
  }

  @Test
  def areSetImproper2 = {
    cookies("BattleKnightSession") = "test1"
    assertFalse(cookies.areSet())
  }

  @Test
  def areSetImproper3 = {
    cookies("BattleKnight") = ""
    cookies("BattleKnightSession") = ""
    assertFalse(cookies.areSet())
  }

  @Test
  def areSetImproper4 = {
    cookies("BattleKnight") = "test1"
    cookies("BattleKnightSession") = "test2"
    cookies.clear()
    assertFalse(cookies.areSet())
  }

  @Test
  def clearTest = {
    cookies.clear()
    cookies.cookies.foreach( eachTuple => {
      assertTrue(cookies.keys.contains(eachTuple._1))
      assertEquals("", cookies(eachTuple._2))
    })
    assertEquals(2, cookies().size)
  }
}
