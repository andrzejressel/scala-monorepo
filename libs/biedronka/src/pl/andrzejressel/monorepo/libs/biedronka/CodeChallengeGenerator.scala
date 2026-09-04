package pl.andrzejressel.monorepo.libs.biedronka

import java.nio.charset.StandardCharsets
import java.security.MessageDigest
import java.util.Base64

object CodeChallengeGenerator {

  def codeChallengeS256(codeVerifier: String): String = {
    val digest = MessageDigest
      .getInstance("SHA-256")
      .digest(codeVerifier.getBytes(StandardCharsets.US_ASCII))

    Base64.getUrlEncoder.withoutPadding.encodeToString(digest)
  }

  def codeChallengePlain(codeVerifier: String): String = {
    Base64.getUrlEncoder.withoutPadding.encodeToString(
      codeVerifier.getBytes(StandardCharsets.US_ASCII)
    )
  }

}
