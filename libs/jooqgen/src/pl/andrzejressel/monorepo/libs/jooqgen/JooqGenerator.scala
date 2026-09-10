package pl.andrzejressel.monorepo.libs.jooqgen

import org.jooq.codegen.GenerationTool
import org.jooq.meta.jaxb.Configuration
import org.jooq.meta.jaxb.Generator
import pl.andrzejressel.monorepo.libs.database.DatabaseConfig
import pl.andrzejressel.monorepo.libs.database.DatabaseSource
import pl.andrzejressel.monorepo.libs.database.MigrationList

import java.nio.file.Files
import java.nio.file.Path
import java.nio.file.Paths

object JooqGenerator {

  def generateToPwd(
      packageName: String,
      migrationList: MigrationList
  ): Unit = {
    val destination = Paths.get(".").toAbsolutePath
    generate(packageName, migrationList, destination)
  }

  def generate(
      packageName: String,
      migrationList: MigrationList,
      destination: Path
  ): Unit = {

    val tempDir = Files.createTempDirectory("jooqgen")

    val databaseConfig = DatabaseConfig(
      dbLocation = tempDir.resolve("temp.db").toAbsolutePath
    )
    val databaseSource = DatabaseSource(
      config = databaseConfig,
      migrationList = migrationList
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
                packageName
              )
              .withDirectory(
                destination.toString
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
