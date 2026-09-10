package pl.andrzejressel.monorepo.apps.shoppingmanager.database.generator

import org.jooq.codegen.DefaultGeneratorStrategy
import org.jooq.codegen.GeneratorStrategy.Mode
import org.jooq.meta.{Definition, ForeignKeyDefinition, InverseForeignKeyDefinition}

class SelfReferenceAwareGeneratorStrategy extends DefaultGeneratorStrategy {

  override def getJavaMethodName(definition: Definition, mode: Mode): String = {
    definition match {
      case fk: ForeignKeyDefinition if isSelfReferencing(fk) =>
        "parent" + super.getJavaMethodName(definition, mode).capitalize

      case ifk: InverseForeignKeyDefinition if isSelfReferencing(ifk.getForeignKey) =>
        "child" + super.getJavaMethodName(definition, mode).capitalize

      case _ =>
        super.getJavaMethodName(definition, mode)
    }
  }

  private def isSelfReferencing(fk: ForeignKeyDefinition): Boolean =
    fk.getTable == fk.getReferencedTable
}