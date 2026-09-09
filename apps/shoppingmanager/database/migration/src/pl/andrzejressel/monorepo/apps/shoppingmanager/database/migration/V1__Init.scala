package pl.andrzejressel.monorepo.apps.shoppingmanager.database.migration

import org.flywaydb.core.api.migration.BaseJavaMigration
import org.flywaydb.core.api.migration.Context

object V1__Init extends BaseJavaMigration {
  override def migrate(context: Context): Unit = {
    val stmt = context.getConnection.createStatement()
    stmt.execute(
      """CREATE TABLE person
        |(
        |    id INTEGER PRIMARY KEY AUTOINCREMENT,
        |    name TEXT NOT NULL
        |)""".stripMargin
    ): Unit
  }
}
