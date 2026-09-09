package pl.andrzejressel.monorepo.apps.shoppingmanager.database.migration

import org.flywaydb.core.api.migration.BaseJavaMigration
import org.flywaydb.core.api.migration.Context

object V4__CreateProducts extends BaseJavaMigration {
  override def migrate(context: Context): Unit = {
    val stmt = context.getConnection.createStatement()
    stmt.execute(
      """CREATE TABLE products
        |(
        |    id   TEXT PRIMARY KEY,
        |    name TEXT NOT NULL
        |)""".stripMargin
    ): Unit
    stmt.execute("DROP TABLE transaction_items"): Unit
    stmt.execute(
      """CREATE TABLE transaction_items
        |(
        |    transaction_id TEXT NOT NULL REFERENCES transactions (id),
        |    position       TEXT NOT NULL,
        |    product_id     TEXT NOT NULL REFERENCES products (id),
        |    PRIMARY KEY (transaction_id, position)
        |)""".stripMargin
    ): Unit
    stmt.execute("""
        |CREATE TABLE store_products
        |(
        |   store_type  TEXT NOT NULL,
        |   id          TEXT NOT NULL,
        |   name        TEXT NOT NULL,
        |   product_id  TEXT NOT NULL REFERENCES products (id),
        |   PRIMARY KEY (store_type, id)
        |)
        |""".stripMargin): Unit
  }
}
