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

import uk.gov.hmrc.apisubscriptionfields.model.{Fields, SubscriptionIdentifier}

object SubscriptionFields {
  def apply(id: SubscriptionIdentifier, fieldsId: UUID, fields: Fields): SubscriptionFields =
    new SubscriptionFields(id.encode(), id.applicationId.value, id.apiContext.value, id.apiVersion.value, fieldsId, fields)
}

case class SubscriptionFields(/* TODO: remove id */ id: String, applicationId: String, apiContext: String, apiVersion: String, fieldsId: UUID, fields: Fields)
