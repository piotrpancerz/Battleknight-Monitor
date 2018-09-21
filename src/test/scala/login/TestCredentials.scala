package login

import org.junit.Assert._
import org.junit._
import settings.Settings._

class TestCredentials {
  private var creds: LoginCredentials = _
  private val username = "test user with spaces"
  private val password = "random123"
  private val server = SERVER_NEWEST + 10

  @Before
  def createLoginCredentialsInstance = creds = LoginCredentials(username, password, server)

  @Test
  def parsingSpacesInUsername = {
    assertEquals("test%20user%20with%20spaces", creds.parsedUsername)
  }

  @Test
  def lengthOfEncryptedPassword = {
    assertEquals(32, creds.encryptedPassword.length)
  }

  @Test
  def chosenServerExceedsRange = {
    assertEquals(SERVER_DEFAULT_VALUE, creds.server)
  }
}
