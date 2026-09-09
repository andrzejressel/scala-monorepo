package pl.andrzejressel.monorepo.apps.shoppingmanager.database.migration

import org.flywaydb.core.api.migration.JavaMigration
import pl.andrzejressel.monorepo.libs.database.MigrationList

object ShoppingManagerMigrationList extends MigrationList {

  override def getMigrations(): Seq[JavaMigration] = Seq(
    V1__Init,
    V2__Seed,
    V3__CreateTransactions,
    V4__CreateProducts,
    V5__AddTransactionItemDetails,
    V6__TransactionItemsReferenceStoreProduct,
    V7__BiedronkaAccount,
    V8__CreateCategories
  )
}
