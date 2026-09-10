package pl.andrzejressel.monorepo.libs.jooqgen

import org.flywaydb.core.api.migration.BaseJavaMigration
import org.flywaydb.core.api.migration.JavaMigration
import pl.andrzejressel.monorepo.libs.database.MigrationList

import java.nio.file.Files

class JooqGeneratorTest extends munit.FunSuite {

  test("Should generate JOOQ classes") {
    val packageName = "pl.andrzejressel.monorepo.libs.jooqgen.generated"
    val tempDir = Files.createTempDirectory("jooqgen")
    JooqGenerator.generate(packageName, TestMigrationList, tempDir)

    assert(
      Files.exists(
        tempDir.resolve(
          "pl/andrzejressel/monorepo/libs/jooqgen/generated/tables/TestTable.scala"
        )
      )
    )
  }

  private object TestMigrationList extends MigrationList {
    override def getMigrations(): Seq[JavaMigration] = Seq(V1__TestMigration)
  }

  private object V1__TestMigration extends BaseJavaMigration {
    override def migrate(
        context: org.flywaydb.core.api.migration.Context
    ): Unit = {
      val connection = context.getConnection
      val statement = connection.createStatement()
      statement.execute(
        "CREATE TABLE test_table (id INTEGER PRIMARY KEY, name TEXT);"
      )
      statement.close()
    }
  }

}
