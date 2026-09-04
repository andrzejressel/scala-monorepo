package pl.andrzejressel.monorepo.libs.biedronka

import io.circe.Decoder
import io.circe.parser.decode
import org.apache.hc.client5.http.classic.methods.HttpGet
import org.apache.hc.client5.http.impl.classic.HttpClients
import org.apache.hc.core5.http.io.entity.EntityUtils
import org.apache.hc.core5.net.URIBuilder

import java.net.URI
import java.net.URL

import BiedronkaApiModels.given

class BiedronkaAPI(
    private val baseUrl: URL,
    private val accessToken: AccessToken
) {

  def listTransactions(page: Int = 1): TransactionListResponse = {
    val uri = new URIBuilder(baseUrl.toURI)
      .appendPath("/api/v6/transactions/")
      .addParameter("page", page.toString)
      .build()

    executeGet[TransactionListResponse](uri)
  }

  def listArchivedTransactions(page: Int = 1): TransactionListResponse = {
    val uri = new URIBuilder(baseUrl.toURI)
      .appendPath("/api/v6/transactions/archived/")
      .addParameter("page", page.toString)
      .build()

    executeGet[TransactionListResponse](uri)
  }

  def getTransaction(id: String): TransactionDetail = {
    val uri = new URIBuilder(baseUrl.toURI)
      .appendPath(s"/api/v6/transactions/$id/")
      .build()

    executeGet[TransactionDetail](uri)
  }

  def getMe: UserProfile = {
    val uri = new URIBuilder(baseUrl.toURI)
      .appendPath("/api/v6/users/me/")
      .addParameter("refresh", "false")
      .build()

    executeGet[UserProfile](uri)
  }

  private def executeGet[T: Decoder](uri: URI): T = {
    val httpClient = HttpClients.createDefault()
    val httpGet = HttpGet(uri)
    httpGet.addHeader("Authorization", s"Bearer ${accessToken.value}")

    httpClient.execute(
      httpGet,
      response => {
        val statusCode = response.getCode
        val responseBody = Option(response.getEntity)
          .map(EntityUtils.toString)
          .getOrElse("")

        if statusCode < 200 || statusCode >= 300 then
          throw new IllegalStateException(
            s"API request failed with status $statusCode: $responseBody"
          )

        decode[T](responseBody).fold(
          error =>
            throw new IllegalArgumentException(
              "Unable to decode API response",
              error
            ),
          identity
        )
      }
    )
  }

}

object BiedronkaAPI {
  def production(accessToken: AccessToken): BiedronkaAPI =
    BiedronkaAPI(
      baseUrl = new URI("https://api.prod.biedronka.cloud").toURL,
      accessToken = accessToken
    )
}
