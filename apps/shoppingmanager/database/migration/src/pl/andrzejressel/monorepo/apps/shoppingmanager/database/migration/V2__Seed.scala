package pl.andrzejressel.monorepo.apps.shoppingmanager.database.migration

import org.flywaydb.core.api.migration.BaseJavaMigration
import org.flywaydb.core.api.migration.Context

object V2__Seed extends BaseJavaMigration {
  override def migrate(context: Context): Unit = {
    val stmt = context.getConnection.createStatement()
    stmt.execute("INSERT INTO person (name) VALUES ('Ada Lovelace')"): Unit
    stmt.execute("INSERT INTO person (name) VALUES ('Alan Turing')"): Unit
  }
}
