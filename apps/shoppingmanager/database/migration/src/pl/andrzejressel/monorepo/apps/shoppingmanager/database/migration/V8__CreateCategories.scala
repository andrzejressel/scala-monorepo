package pl.andrzejressel.monorepo.apps.shoppingmanager.database.migration

import org.flywaydb.core.api.migration.BaseJavaMigration
import org.flywaydb.core.api.migration.Context

object V8__CreateCategories extends BaseJavaMigration {
  override def migrate(context: Context): Unit = {
    val stmt = context.getConnection.createStatement()
    stmt.execute(
      """CREATE TABLE categories
        |(
        |    id        TEXT PRIMARY KEY,
        |    name      TEXT NOT NULL,
        |    parent_id TEXT REFERENCES categories (id)
        |)""".stripMargin
    ): Unit
    stmt.execute(
      "ALTER TABLE products ADD COLUMN category_id TEXT REFERENCES categories (id)"
    ): Unit
  }
}
