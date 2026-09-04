package pl.andrzejressel.monorepo.libs.biedronka

import munit.FunSuite

class CodeChallengeGeneratorTest extends FunSuite {

  test("codeChallengeS256 should generate valid challenge") {
    val codeVerifier = "UuDmeH16R-2ogYBEn3-3p_OPuD8S74y8b19N0tP9FXY"
    val expectedChallenge = "uWSeqMpQlCbzSMhnie_4Ru1bOM7axX-0xn5Xddfc2go"

    val actualChallenge = CodeChallengeGenerator.codeChallengeS256(codeVerifier)

    assertEquals(actualChallenge, expectedChallenge)
  }

}
