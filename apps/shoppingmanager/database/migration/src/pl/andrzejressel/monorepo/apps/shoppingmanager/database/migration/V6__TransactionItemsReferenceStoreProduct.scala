package pl.andrzejressel.monorepo.apps.shoppingmanager.database.migration

import org.flywaydb.core.api.migration.BaseJavaMigration
import org.flywaydb.core.api.migration.Context

object V6__TransactionItemsReferenceStoreProduct extends BaseJavaMigration {
  override def migrate(context: Context): Unit = {
    val stmt = context.getConnection.createStatement()
    stmt.execute("DROP TABLE transaction_items"): Unit
    stmt.execute(
      """CREATE TABLE transaction_items
        |(
        |    transaction_id                TEXT NOT NULL REFERENCES transactions (id),
        |    position                      TEXT NOT NULL,
        |    store_type                    TEXT NOT NULL,
        |    store_product_id              TEXT NOT NULL,
        |    quantity                      REAL NOT NULL,
        |    unit_price                    REAL NOT NULL,
        |    total_discount                REAL NOT NULL,
        |    total_price_without_discount  REAL NOT NULL,
        |    total_price                   REAL NOT NULL,
        |    PRIMARY KEY (transaction_id, position),
        |    FOREIGN KEY (store_type, store_product_id) REFERENCES store_products (store_type, id)
        |)""".stripMargin
    ): Unit
  }
}
