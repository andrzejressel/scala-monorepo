package pl.andrzejressel.monorepo.libs.database

import org.flywaydb.core.api.migration.{BaseJavaMigration, Context, JavaMigration}

class DatabaseTest extends munit.FunSuite {

  test("Can migrate sqlite database") {

    val tempFile = java.nio.file.Files.createTempFile("test", ".db")

    val config = DatabaseConfig(
      dbLocation = tempFile
    )

    val databaseSource = new DatabaseSource(
      config = config,
      migrationList = TestMigrationList
    )

    val ds = databaseSource.createDataSource()

    val conn = ds.getConnection()

    val stmt = conn.createStatement()
    val rs = stmt.executeQuery("SELECT name FROM sqlite_master WHERE type='table' AND name='test_table';")
    assert(rs.next(), "Table 'test_table' should exist after migration")

  }

  private object TestMigrationList extends MigrationList {
    override def getMigrations(): Seq[JavaMigration] = Seq(
      V1__TestMigration
    )
  }

  private object V1__TestMigration extends BaseJavaMigration {
    override def migrate(connection: Context): Unit = {
      val stmt = connection.getConnection.createStatement()
      stmt.execute("CREATE TABLE test_table (id INTEGER PRIMARY KEY, name TEXT)")
    }

  }


}
