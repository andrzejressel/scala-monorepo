package pl.andrzejressel.monorepo.libs.biedronka

import com.github.tomakehurst.wiremock.client.WireMock.*
import munit.FunSuite
import pl.andrzejressel.monorepo.libs.wiremocktestkit.WiremockFixtureSuite

import java.net.URI

class BiedronkaAPITest extends FunSuite with WiremockFixtureSuite {

  wiremock.test("listTransactions should return paginated results") {
    wiremock =>
      val responseJson = """{
      "transactions": [
        {
          "id": "tx1",
          "date": "2026-08-04T10:00:00Z",
          "total_price": 123.45,
          "store_name": "Biedronka #123",
          "receipt_num": "RCP-001",
          "is_e_receipt_available": true
        }
      ],
      "page_number": 1,
      "page_count": 5,
      "previous_page": null,
      "next_page": 2
    }"""

      wiremock.stubFor(
        get(urlPathEqualTo("/api/v6/transactions/"))
          .withQueryParam("page", equalTo("1"))
          .willReturn(
            okJson(responseJson)
              .withHeader("Content-Type", "application/json")
          )
      )

      val api = BiedronkaAPI(
        baseUrl = URI(wiremock.baseUrl()).toURL,
        accessToken = AccessToken("test-token")
      )

      val result = api.listTransactions(page = 1)

      assertEquals(result.transactions.length, 1)
      assertEquals(result.transactions(0).id, "tx1")
      assertEquals(result.transactions(0).totalPrice, 123.45)
      assertEquals(result.pageNumber, 1)
      assertEquals(result.pageCount, 5)
      assertEquals(result.nextPage, Some(2))
      assertEquals(result.previousPage, None)
  }

  wiremock.test(
    "listArchivedTransactions should return archived transactions"
  ) { wiremock =>
    val responseJson = """{
      "transactions": [
        {
          "id": "archived1",
          "date": "2026-01-15T14:30:00Z",
          "total_price": 456.78,
          "store_name": "Biedronka #456",
          "receipt_num": "RCP-002",
          "is_e_receipt_available": false
        }
      ],
      "page_number": 1,
      "page_count": 3,
      "previous_page": null,
      "next_page": 2
    }"""

    wiremock.stubFor(
      get(urlPathEqualTo("/api/v6/transactions/archived/"))
        .withQueryParam("page", equalTo("1"))
        .willReturn(okJson(responseJson))
    )

    val api = BiedronkaAPI(
      baseUrl = URI(wiremock.baseUrl()).toURL,
      accessToken = AccessToken("test-token")
    )

    val result = api.listArchivedTransactions()

    assertEquals(result.transactions.length, 1)
    assertEquals(result.transactions(0).id, "archived1")
    assertEquals(result.transactions(0).isEReceiptAvailable, false)
  }

  wiremock.test("getTransaction should return full transaction details") {
    wiremock =>
      val responseJson = """{
      "id": "tx-detail",
      "date": "2026-07-20T16:00:00Z",
      "total_price": 789.01,
      "store_name": "Biedronka #789",
      "receipt_num": "RCP-789",
      "is_e_receipt_available": true,
      "total_discount": 50.0,
      "store_id": "S123",
      "cash_register_id": 5,
      "id_from_receipt": "RID-789",
      "cashier_id": "C456",
      "basket_id": "B789",
      "invoice_id": null,
      "total_tax": 120.50,
      "due_change": 10.99,
      "store": {
        "street": "ul. Przykladowa 10",
        "zip_code": "00-001",
        "city": "Warszawa"
      },
      "items": [
        {
          "position": "1",
          "name": "Mleko",
          "quantity": 1.0,
          "unit_price": 3.99,
          "total_discount": 0.0,
          "total_price_without_discount": 3.99,
          "total_price": 3.99,
          "ean": "5901234123457",
          "vat_rate": 23,
          "vat_fiscal_code": "VAT23",
          "measure_unit": "szt"
        }
      ],
      "payments": [
        {
          "payment_type": "CARD",
          "name": "Karta kredytowa",
          "value": 800.0,
          "guid": "550e8400-e29b-41d4-a716-446655440000"
        }
      ],
      "tax_summaries": [
        {
          "vat_rate": 23,
          "sale_value": 523.91,
          "tax_value": 120.50,
          "vat_fiscal_code": "VAT23"
        }
      ],
      "receipt_barcode": "123456789",
      "extended_transaction_number": "EXTX-789",
      "collected_returnable_packagings_value": 5.0,
      "sold_returnable_packagings_value": 2.0,
      "payment_rounding": 0.0
    }"""

      wiremock.stubFor(
        get(urlPathEqualTo("/api/v6/transactions/tx-detail/"))
          .willReturn(okJson(responseJson))
      )

      val api = BiedronkaAPI(
        baseUrl = URI(wiremock.baseUrl()).toURL,
        accessToken = AccessToken("test-token")
      )

      val result = api.getTransaction("tx-detail")

      assertEquals(result.id, "tx-detail")
      assertEquals(result.totalPrice, 789.01)
      assertEquals(result.totalDiscount, 50.0)
      assertEquals(result.storeId, "S123")
      assertEquals(result.store.city, "Warszawa")
      assertEquals(result.items.length, 1)
      assertEquals(result.items(0).name, "Mleko")
      assertEquals(result.payments.length, 1)
      assertEquals(result.taxSummaries.length, 1)
  }

  wiremock.test("should include Bearer token in request headers") { wiremock =>
    val responseJson = """{
      "transactions": [],
      "page_number": 1,
      "page_count": 1,
      "previous_page": null,
      "next_page": null
    }"""

    wiremock.stubFor(
      get(urlPathEqualTo("/api/v6/transactions/"))
        .withHeader("Authorization", equalTo("Bearer my-special-token"))
        .willReturn(okJson(responseJson))
    )

    val api = BiedronkaAPI(
      baseUrl = URI(wiremock.baseUrl()).toURL,
      accessToken = AccessToken("my-special-token")
    )

    val _ = api.listTransactions()

    wiremock.verify(
      getRequestedFor(urlPathEqualTo("/api/v6/transactions/"))
        .withHeader("Authorization", equalTo("Bearer my-special-token"))
    )
  }

  wiremock.test("should throw on 404 response") { wiremock =>
    wiremock.stubFor(
      get(urlPathEqualTo("/api/v6/transactions/missing/"))
        .willReturn(notFound().withBody("Transaction not found"))
    )

    val api = BiedronkaAPI(
      baseUrl = URI(wiremock.baseUrl()).toURL,
      accessToken = AccessToken("test-token")
    )

    intercept[IllegalStateException] {
      api.getTransaction("missing")
    }
  }

  wiremock.test("getMe should return the user profile") { wiremock =>
    val responseJson = """{
      "card_number": "000000000",
      "consents": {
        "ereceipt_consents": true,
        "lookalike_audiences_data_sharing_consent": false,
        "no_coupon_paper_printouts_consent": true,
        "partner_data_sharing_consent": false,
        "phone_number_pos_authorization_consent": true,
        "remarketing_consent": false,
        "retargeting_mobile_app_consent": false,
        "third_party_marketing_others_consent": false,
        "tobacco_marketing_consent": false
      },
      "date_of_birth": null,
      "electronic_carrier": false,
      "email": "user@example.com",
      "feature_flags": {
        "blik_payments": true,
        "ereceipt_consent": true,
        "mobile_auth": true,
        "product_availability": true,
        "recycler_wallet": true,
        "retail_media": false,
        "retail_media_consents": true,
        "retail_media_send_data": false
      },
      "first_name": "FirstName",
      "gdpr": false,
      "gender": "Prefer not to say",
      "is_blik_payment_active": false,
      "is_show_tobacco": false,
      "last_name": null,
      "market": {
        "code": "pl",
        "timezone": "Europe/Warsaw"
      },
      "old_app_deactivated": null,
      "pdf_417_string": "PDF417_STRING",
      "perspectiv_id": "PERSPECTIV_ID",
      "phone_number": "000000000",
      "store": {
        "city": "City",
        "code": "0000",
        "is_closed_now": false,
        "is_in_refurbishment": false,
        "latitude": 0.0,
        "longitude": 0.0,
        "name": "NAME",
        "opening_hours": [
          {
            "closing_time": "22:30",
            "date": "2026-08-22",
            "opening_time": "06:00",
            "store_is_closed": false
          }
        ],
        "segment": "With meat counter",
        "street": "ul. Example Street 1",
        "target_hour": "2026-08-22T20:30:00Z",
        "zip_code": "00-001"
      },
      "wallets": [
        {
          "balance": 0.0,
          "perspectiv_id": "kropki"
        }
      ]
    }"""

    wiremock.stubFor(
      get(urlPathEqualTo("/api/v6/users/me/"))
        .withQueryParam("refresh", equalTo("false"))
        .willReturn(okJson(responseJson))
    )

    val api = BiedronkaAPI(
      baseUrl = URI(wiremock.baseUrl()).toURL,
      accessToken = AccessToken("test-token")
    )

    val result = api.getMe

    assertEquals(result.email, "user@example.com")
    assertEquals(result.firstName, Some("FirstName"))
    assertEquals(result.lastName, None)
  }

  wiremock.test("should throw on malformed JSON response") { wiremock =>
    wiremock.stubFor(
      get(urlPathEqualTo("/api/v6/transactions/"))
        .withQueryParam("page", equalTo("1"))
        .willReturn(ok("{invalid json"))
    )

    val api = BiedronkaAPI(
      baseUrl = URI(wiremock.baseUrl()).toURL,
      accessToken = AccessToken("test-token")
    )

    intercept[IllegalArgumentException] {
      api.listTransactions()
    }
  }
}
