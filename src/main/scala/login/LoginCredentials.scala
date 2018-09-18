package login

import settings.Settings._

case class LoginCredentials(username: String, password: String, server: Int = SERVER_DEFAULT_VALUE)
