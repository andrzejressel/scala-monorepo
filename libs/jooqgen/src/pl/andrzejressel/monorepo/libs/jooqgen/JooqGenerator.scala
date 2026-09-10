package pl.andrzejressel.monorepo.libs.jooqgen

import org.jooq.codegen.{GenerationTool, Scala3Generator}
import org.jooq.meta.jaxb.{Configuration, Generator, Strategy}
import org.jooq.meta.sqlite.SQLiteDatabase
import pl.andrzejressel.monorepo.libs.database.DatabaseConfig
import pl.andrzejressel.monorepo.libs.database.DatabaseSource
import pl.andrzejressel.monorepo.libs.database.MigrationList

import java.nio.file.Files
import java.nio.file.Path
import java.nio.file.Paths

object JooqGenerator {

  def generateToPwd(
      packageName: String,
      migrationList: MigrationList,
      strategy: Option[Strategy] = None
  ): Unit = {
    val destination = Paths.get(".").toAbsolutePath
    generate(packageName, migrationList, destination, strategy)
  }

  private[jooqgen] def generate(
      packageName: String,
      migrationList: MigrationList,
      destination: Path,
      strategy: Option[Strategy]
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
          .withStrategy(strategy.orNull)
          .withDatabase(
            org.jooq.meta.jaxb
              .Database()
              .withName(classOf[SQLiteDatabase].getName)
              .withIncludes(".*")
              .withExcludes("flyway_schema_history")
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
          .withName(classOf[Scala3Generator].getName)
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
