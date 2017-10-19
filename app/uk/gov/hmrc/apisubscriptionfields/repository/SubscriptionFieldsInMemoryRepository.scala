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

import java.util.UUID

import scala.concurrent.Future

//TODO we think this should be removed
class SubscriptionFieldsInMemoryRepository extends SubscriptionFieldsRepository {
  private[this] var values: Map[String, SubscriptionFields] = Map()
  private[this] var altKey: Map[UUID, String] = Map()

  override def upsert(subscription: SubscriptionFields): Future[Boolean] = {
    val isInserted = values.contains(subscription.id)
    values = values + ((subscription.id, subscription))
    altKey = altKey + ((subscription.fieldsId, subscription.id))
    Future.successful(isInserted)
  }

  override def fetchByApplicationId(applicationId: String): Future[List[SubscriptionFields]] = ???

  override def fetchById(identifier: String): Future[Option[SubscriptionFields]] = {
    Future.successful(values.get(identifier))
  }

  override def fetchByFieldsId(fieldsId: UUID): Future[Option[SubscriptionFields]] = {
    Future.successful(
      for {
        id <- altKey.get(fieldsId)
        sub <- values.get(id)
      } yield sub)
  }

  override def delete(id: String): Future[Boolean] = {
    values = values - id
    altKey = altKey.filterNot( pair => pair._2 == id )
    Future.successful(true)
  }

}
