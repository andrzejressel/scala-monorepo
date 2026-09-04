package pl.andrzejressel.monorepo.libs.biedronka

import io.circe.parser.decode
import org.apache.commons.lang3.RandomStringUtils
import org.apache.hc.client5.http.classic.methods.HttpPost
import org.apache.hc.client5.http.entity.UrlEncodedFormEntity
import org.apache.hc.client5.http.impl.classic.HttpClients
import org.apache.hc.core5.http.io.entity.EntityUtils
import org.apache.hc.core5.http.message.BasicNameValuePair
import org.apache.hc.core5.net.URIBuilder

import java.net.URI
import java.net.URL
import scala.jdk.CollectionConverters.given

class BiedronkaAuth(
    private val baseUrl: URL,
    private val verifier: String
) {

  def generateLoginURL(): URL = {
    new URIBuilder(baseUrl.toURI)
      .appendPath("/realms/loyalty/protocol/openid-connect/auth")
      .addParameter("response_type", "code")
      .addParameter("client_id", BiedronkaAuth.clientId)
      .addParameter("redirect_uri", BiedronkaAuth.redirectUri)
      .addParameter("code_challenge", challenge)
      .addParameter("code_challenge_method", BiedronkaAuth.codeChallengeMethod)
      .build()
      .toURL
  }

  private val challenge = CodeChallengeGenerator.codeChallengeS256(verifier)
  private val tokenEndpoint = new URIBuilder(baseUrl.toURI)
    .appendPath("/realms/loyalty/protocol/openid-connect/token")
    .build()

  def createTokens(redirectUri: URI): BiedronkaTokens = {
    val uriBuilder = new URIBuilder(redirectUri)
    val code = uriBuilder.getFirstQueryParam("code").getValue

    val params = Seq(
      new BasicNameValuePair("grant_type", "authorization_code"),
      new BasicNameValuePair("client_id", BiedronkaAuth.clientId),
      new BasicNameValuePair("redirect_uri", BiedronkaAuth.redirectUri),
      new BasicNameValuePair("code", code),
      new BasicNameValuePair(
        "code_verifier",
        verifier
      )
    )

    executeTokenRequest(params)
  }

  def refreshTokens(refreshToken: RefreshToken): BiedronkaTokens = {
    val params = Seq(
      new BasicNameValuePair("grant_type", "refresh_token"),
      new BasicNameValuePair("client_id", BiedronkaAuth.clientId),
      new BasicNameValuePair("redirect_uri", BiedronkaAuth.redirectUri),
      new BasicNameValuePair("refresh_token", refreshToken.value)
    )

    executeTokenRequest(params)
  }

  private def executeTokenRequest(
      params: Seq[BasicNameValuePair]
  ): BiedronkaTokens = {
    val httpClient = HttpClients.createDefault()
    val httpPost = HttpPost(tokenEndpoint)
    httpPost.setEntity(new UrlEncodedFormEntity(params.asJava))

    val body = httpClient.execute(
      httpPost,
      response => {
        val statusCode = response.getCode
        val responseBody = Option(response.getEntity)
          .map(EntityUtils.toString)
          .getOrElse("")

        if statusCode < 200 || statusCode >= 300 then
          throw new IllegalStateException(
            s"Token endpoint request failed with status $statusCode: $responseBody"
          )

        responseBody
      }
    )

    BiedronkaAuth.parseTokensJson(body)
  }

}

object BiedronkaAuth {

  def production(): BiedronkaAuth =
    BiedronkaAuth(
      baseUrl = new URI("https://konto.biedronka.pl").toURL,
      verifier = RandomStringUtils.insecure().nextAlphanumeric(43)
    )

  private[biedronka] def parseTokensJson(body: String): BiedronkaTokens = {
    decode[BiedronkaTokens](body).fold(
      error =>
        throw new IllegalArgumentException(
          "Unable to decode token response",
          error
        ),
      identity
    )
  }

  private val clientId = "cma20"
  private val redirectUri = "app://cma20.biedronka.pl"
  private val codeChallengeMethod = "S256"
}
