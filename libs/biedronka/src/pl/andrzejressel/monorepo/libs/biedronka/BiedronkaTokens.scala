package pl.andrzejressel.monorepo.libs.biedronka

import io.circe.Decoder
import io.circe.derivation.Configuration

opaque type AccessToken = String
object AccessToken {
  def apply(token: String): AccessToken = token
  extension (token: AccessToken) {
    def value: String = token
  }
}

opaque type RefreshToken = String
object RefreshToken {
  def apply(token: String): RefreshToken = token
  extension (token: RefreshToken) {
    def value: String = token
  }
}

case class BiedronkaTokens(accessToken: AccessToken, refreshToken: RefreshToken)

object BiedronkaTokens {
  private given Configuration = Configuration.default.withSnakeCaseMemberNames

  private given Decoder[AccessToken] =
    Decoder.decodeString.map(AccessToken.apply)

  private given Decoder[RefreshToken] =
    Decoder.decodeString.map(RefreshToken.apply)

  private[biedronka] given Decoder[BiedronkaTokens] =
    Decoder.derivedConfigured[BiedronkaTokens]
}
