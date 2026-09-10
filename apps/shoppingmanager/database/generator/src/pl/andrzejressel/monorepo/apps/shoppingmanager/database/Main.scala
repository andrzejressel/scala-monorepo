package pl.andrzejressel.monorepo.apps.shoppingmanager.database

import pl.andrzejressel.monorepo.apps.shoppingmanager.database.migration.ShoppingManagerMigrationList
import pl.andrzejressel.monorepo.libs.jooqgen.JooqGenerator

object Main {
  def main(args: Array[String]): Unit = {
    JooqGenerator.generateToPwd(
      "pl.andrzejressel.monorepo.apps.shoppingmanager.database.jooq",
      ShoppingManagerMigrationList
    )
  }
}
