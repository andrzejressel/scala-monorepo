package pl.andrzejressel.monorepo.apps.shoppingmanager.database.migration

import org.flywaydb.core.api.migration.BaseJavaMigration
import org.flywaydb.core.api.migration.Context

object V3__CreateTransactions extends BaseJavaMigration {
  override def migrate(context: Context): Unit = {
    val stmt = context.getConnection.createStatement()
    stmt.execute(
      """CREATE TABLE transactions
        |(
        |    id   TEXT PRIMARY KEY,
        |    date TEXT NOT NULL
        |)""".stripMargin
    ): Unit
    stmt.execute(
      """CREATE TABLE transaction_items
        |(
        |    transaction_id TEXT NOT NULL REFERENCES transactions (id),
        |    position       TEXT NOT NULL,
        |    name           TEXT NOT NULL,
        |    ean            TEXT NOT NULL
        |)""".stripMargin
    ): Unit
  }
}
