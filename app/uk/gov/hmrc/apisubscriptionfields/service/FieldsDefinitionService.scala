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

package uk.gov.hmrc.apisubscriptionfields.service

import javax.inject.{Inject, Singleton}

import play.api.Logger
import uk.gov.hmrc.apisubscriptionfields.model._
import uk.gov.hmrc.apisubscriptionfields.repository.{FieldsDefinition, FieldsDefinitionRepository}

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future

@Singleton
class FieldsDefinitionService @Inject() (repository: FieldsDefinitionRepository) {

  def upsert(identifier: FieldsDefinitionIdentifier, fields: Seq[FieldDefinition]): Future[Boolean] = {
    Logger.debug(s"[upsert] FieldsDefinitionIdentifier: $identifier")

    repository.save(FieldsDefinition(identifier.encode(), identifier.apiContext.value, identifier.apiVersion.value, fields))
  }

  def get(identifier: FieldsDefinitionIdentifier): Future[Option[FieldsDefinitionResponse]] = {
    Logger.debug(s"[get] FieldsDefinitionIdentifier: $identifier")
    for {
      fetch <- repository.fetchById(identifier)
    } yield fetch.map(asResponse)
  }

  private def asResponse(fieldsDefinition: FieldsDefinition): FieldsDefinitionResponse = {
    FieldsDefinitionResponse(fields = fieldsDefinition.fields)
  }
}
