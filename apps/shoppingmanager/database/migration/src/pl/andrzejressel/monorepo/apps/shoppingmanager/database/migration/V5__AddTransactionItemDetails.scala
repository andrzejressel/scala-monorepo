package pl.andrzejressel.monorepo.apps.shoppingmanager.database.migration

import org.flywaydb.core.api.migration.BaseJavaMigration
import org.flywaydb.core.api.migration.Context

object V5__AddTransactionItemDetails extends BaseJavaMigration {
  override def migrate(context: Context): Unit = {
    val stmt = context.getConnection.createStatement()
    stmt.execute(
      "ALTER TABLE transaction_items ADD COLUMN quantity REAL NOT NULL DEFAULT 0"
    ): Unit
    stmt.execute(
      "ALTER TABLE transaction_items ADD COLUMN unit_price REAL NOT NULL DEFAULT 0"
    ): Unit
    stmt.execute(
      "ALTER TABLE transaction_items ADD COLUMN total_discount REAL NOT NULL DEFAULT 0"
    ): Unit
    stmt.execute(
      "ALTER TABLE transaction_items ADD COLUMN total_price_without_discount REAL NOT NULL DEFAULT 0"
    ): Unit
    stmt.execute(
      "ALTER TABLE transaction_items ADD COLUMN total_price REAL NOT NULL DEFAULT 0"
    ): Unit
  }
}
