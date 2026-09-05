package pl.andrzejressel.monorepo.libs.database

import org.flywaydb.core.Flyway
import org.sqlite.SQLiteConfig
import org.sqlite.SQLiteConfig.Pragma
import org.sqlite.javax.SQLiteConnectionPoolDataSource
import pl.andrzejressel.monorepo.libs.logging.Logging

import javax.sql.DataSource

class DatabaseSource(
    private val config: DatabaseConfig,
    private val migrationList: MigrationList
) extends Logging {

  def createDataSource(): DataSource = {

    val jdbcUrl = s"jdbc:sqlite:${config.dbLocation}"
    val sqLiteConfig = SQLiteConfig()
    sqLiteConfig.enforceForeignKeys(true)
    sqLiteConfig.setPragma(Pragma.JOURNAL_MODE, "WAL")

    val ds = SQLiteConnectionPoolDataSource(sqLiteConfig)
    ds.setUrl(jdbcUrl)

    runMigration(ds)

    ds
  }

  private def runMigration(ds: DataSource): Unit = {
    val flyway = Flyway
      .configure()
      .dataSource(ds)
      .javaMigrations(
        migrationList.getMigrations()*
      )
      .load()

    val result = flyway.migrate()

    logger.info(s"Applied ${result.migrationsExecuted} migration(s)")
  }

}
