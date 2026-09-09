package pl.andrzejressel.monorepo.apps.shoppingmanager.database.migration

import pl.andrzejressel.monorepo.libs.database.DatabaseConfig
import pl.andrzejressel.monorepo.libs.database.DatabaseSource

class MigrationTest extends munit.FunSuite {

  test("Can migrate sqlite database") {

    val tempFile = java.nio.file.Files.createTempFile("test", ".db")

    val config = DatabaseConfig(
      dbLocation = tempFile
    )

    val databaseSource = new DatabaseSource(
      config = config,
      migrationList = ShoppingManagerMigrationList
    )

    val ds = databaseSource.createDataSource()

    val conn = ds.getConnection()

    val stmt = conn.createStatement()
    val rs = stmt.executeQuery(
      "SELECT name FROM sqlite_master WHERE type='table' AND name='test_table';"
    )
    assert(rs.next(), "Table 'test_table' should exist after migration")

  }

}
