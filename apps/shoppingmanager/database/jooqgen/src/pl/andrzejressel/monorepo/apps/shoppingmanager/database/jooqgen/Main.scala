package pl.andrzejressel.monorepo.apps.shoppingmanager.database.jooqgen

import org.jooq.codegen.GenerationTool
import org.jooq.meta.jaxb.Configuration
import org.jooq.meta.jaxb.Generator
import pl.andrzejressel.monorepo.apps.shoppingmanager.database.migration.ShoppingManagerMigrationList
import pl.andrzejressel.monorepo.libs.database.DatabaseConfig
import pl.andrzejressel.monorepo.libs.database.DatabaseSource

import java.nio.file.Files
import java.nio.file.Paths

object Main {
  def main(args: Array[String]): Unit = {

    val tempDir = Files.createTempDirectory("jooqgen")

    val databaseConfig = DatabaseConfig(
      dbLocation = tempDir.resolve("temp.db").toAbsolutePath
    )
    val databaseSource = DatabaseSource(
      config = databaseConfig,
      migrationList = ShoppingManagerMigrationList
    )

    val conf = Configuration()
      .withGenerator(
        Generator()
          .withDatabase(
            org.jooq.meta.jaxb
              .Database()
              .withName("org.jooq.meta.sqlite.SQLiteDatabase")
              .withIncludes(".*")
              .withExcludes("")
//              .withInputSchema("main")
          )
          .withTarget(
            org.jooq.meta.jaxb
              .Target()
              .withPackageName(
                "pl.andrzejressel.monorepo.apps.shoppingmanager.database.jooq"
              )
              .withDirectory(
                Paths
                  .get("apps/shoppingmanager/database/generated/src")
                  .toAbsolutePath
                  .toString
              )
          )
          .withName("org.jooq.codegen.Scala3Generator")
          .withGenerate(
            org.jooq.meta.jaxb
              .Generate()
              .withPojos(true)
              .withDaos(true)
              .withEnumsAsScalaEnums(true)
              .withPojosAsScalaCaseClasses(true)
              .withImmutablePojos(true)
          )
      )

    val gt = GenerationTool()
    gt.setDataSource(databaseSource.createDataSource())
    gt.run(conf)
  }
}
