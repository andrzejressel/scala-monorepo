package pl.andrzejressel.monorepo.apps.shoppingmanager.database.migration

import org.flywaydb.core.api.migration.BaseJavaMigration
import org.flywaydb.core.api.migration.Context

object V7__BiedronkaAccount extends BaseJavaMigration {
  override def migrate(context: Context): Unit = {
    val stmt = context.getConnection.createStatement()
    stmt.execute(
      """CREATE TABLE biedronka_accounts
        |(
        |    id            TEXT PRIMARY KEY,
        |    refresh_token TEXT NOT NULL
        |)""".stripMargin
    ): Unit
  }
}
