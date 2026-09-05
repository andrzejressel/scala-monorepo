package pl.andrzejressel.monorepo.libs.database

import org.flywaydb.core.api.migration.JavaMigration

trait MigrationList {

  def getMigrations(): Seq[JavaMigration]

}
