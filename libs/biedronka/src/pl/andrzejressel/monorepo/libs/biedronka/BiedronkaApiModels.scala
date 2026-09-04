package pl.andrzejressel.monorepo.libs.biedronka

import io.circe.Decoder
import io.circe.derivation.Configuration

import java.time.Instant
import java.util.UUID

case class TransactionSummary(
    id: String,
    date: Instant,
    totalPrice: Double,
    storeName: String,
    receiptNum: String,
    isEReceiptAvailable: Boolean
)

case class TransactionListResponse(
    transactions: Seq[TransactionSummary],
    pageNumber: Int,
    pageCount: Int,
    previousPage: Option[Int],
    nextPage: Option[Int]
)

case class Store(
    street: String,
    zipCode: String,
    city: String
)

case class TransactionItem(
    position: String,
    name: String,
    quantity: Double,
    unitPrice: Double,
    totalDiscount: Double,
    totalPriceWithoutDiscount: Double,
    totalPrice: Double,
    ean: String,
    vatRate: Int,
    vatFiscalCode: String,
    measureUnit: String
)

case class Payment(
    paymentType: String,
    name: String,
    value: Double,
    guid: UUID
)

case class TaxSummary(
    vatRate: Int,
    saleValue: Double,
    taxValue: Double,
    vatFiscalCode: String
)

case class TransactionDetail(
    id: String,
    date: Instant,
    totalPrice: Double,
    storeName: String,
    receiptNum: String,
    isEReceiptAvailable: Boolean,
    totalDiscount: Double,
    storeId: String,
    cashRegisterId: Int,
    idFromReceipt: String,
    cashierId: Option[String],
    basketId: Option[String],
    invoiceId: Option[String],
    totalTax: Double,
    dueChange: Double,
    store: Store,
    items: Seq[TransactionItem],
    payments: Seq[Payment],
    taxSummaries: Seq[TaxSummary],
    receiptBarcode: String,
    extendedTransactionNumber: String,
    collectedReturnablePackagingsValue: Double,
    soldReturnablePackagingsValue: Double,
    paymentRounding: Option[Double]
)

case class UserProfile(
    cardNumber: String,
    email: String,
    firstName: Option[String],
    lastName: Option[String]
)

object BiedronkaApiModels {
  private[biedronka] given Configuration =
    Configuration.default.withSnakeCaseMemberNames

  given Decoder[TransactionSummary] = Decoder.derivedConfigured
  given Decoder[TransactionListResponse] = Decoder.derivedConfigured
  given Decoder[Store] = Decoder.derivedConfigured
  given Decoder[TransactionItem] = Decoder.derivedConfigured
  given Decoder[Payment] = Decoder.derivedConfigured
  given Decoder[TaxSummary] = Decoder.derivedConfigured
  given Decoder[TransactionDetail] = Decoder.derivedConfigured
  given Decoder[UserProfile] = Decoder.derivedConfigured
}
