/*
 * Copyright 2017 HM Revenue & Customs
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package uk.gov.hmrc.apisubscriptionfields.repository

import javax.inject.{Inject, Singleton}

import com.google.inject.ImplementedBy
import play.api.Logger
import play.api.libs.json._
import reactivemongo.api.commands.WriteResult
import reactivemongo.api.indexes.Index
import reactivemongo.api.indexes.IndexType.Ascending
import reactivemongo.bson.{BSONDocument, BSONObjectID}
import uk.gov.hmrc.mongo.ReactiveRepository
import uk.gov.hmrc.mongo.json.ReactiveMongoFormats

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future

//TODO: think about getting rid of trait
@ImplementedBy(classOf[FieldsDefinitionMongoRepository])
trait FieldsDefinitionRepository {

  /**
    * Saves or inserts entity depending on if it already exists.
    * Returns Future of isInserted Boolean flag if everything went OK - otherwise a failed Future with an error message.
    * @param fieldsDefinition entity to upsert
    */
  def upsert(fieldsDefinition: FieldsDefinition): Future[Boolean]

  def fetchById(id: String): Future[Option[FieldsDefinition]]
}

@Singleton
class FieldsDefinitionMongoRepository @Inject()(mongoDbProvider: MongoDbProvider)
  extends ReactiveRepository[FieldsDefinition, BSONObjectID]("fieldsDefinitions", mongoDbProvider.mongo,
    MongoFormatters.FieldsDefinitionJF, ReactiveMongoFormats.objectIdFormats)
  with FieldsDefinitionRepository {

  private implicit val format = MongoFormatters.FieldsDefinitionJF

  override def indexes = Seq(
    createSingleFieldAscendingIndex(
      indexFieldKey = "id",
      indexName = Some("idIndex")
    )
  )

  private def createSingleFieldAscendingIndex(indexFieldKey: String, indexName: Option[String],
                                              isUnique: Boolean = false, isBackground: Boolean = true): Index = {
    Index(
      key = Seq(indexFieldKey -> Ascending),
      name = indexName,
      unique = isUnique,
      background = isBackground
    )
  }

  override def fetchById(id: String): Future[Option[FieldsDefinition]] = {
    val selector = selectorById(id)
    Logger.debug(s"[fetchById] selector: $selector")
    collection.find(selector).one[FieldsDefinition]
  }

  /**
    * Saves or inserts entity depending on if it already exists.
    * Returns Future of isInserted Boolean flag if everything went OK - otherwise a failed Future with an error message.
    * @param fieldsDefinition entity to upsert
    */
  def upsert(fieldsDefinition: FieldsDefinition): Future[Boolean] = {
    collection.update(selector = BSONDocument("_id" -> fieldsDefinition.id), update = fieldsDefinition, upsert = true).map {
      updateWriteResult => handleError(updateWriteResult, "", updateWriteResult.upserted.nonEmpty)
    }
  }

  private def handleError(result: WriteResult, exceptionMsg: => String, isInserted: Boolean): Boolean = {

    def databaseAltered(writeResult: WriteResult): Boolean = writeResult.n > 0

    def handleError(result: WriteResult) =
      if (databaseAltered(result))
        isInserted
      else
        throw new RuntimeException(exceptionMsg)

    result.errmsg.fold(handleError(result)) {
      errMsg => {
        val errorMsg = s"""$exceptionMsg. $errMsg"""
        logger.error(errorMsg)
        throw new RuntimeException(errorMsg)
      }
    }
  }

  private def selectorById(id: String) = Json.obj("id" -> id)

}
