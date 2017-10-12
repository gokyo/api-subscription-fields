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

package unit.uk.gov.hmrc.apisubscriptionfields.controller

import org.scalamock.scalatest.MockFactory
import play.api.libs.json.{JsValue, Json}
import play.api.mvc._
import play.api.test.Helpers._
import play.api.test._
import uk.gov.hmrc.apisubscriptionfields.controller.FieldsDefinitionController
import uk.gov.hmrc.apisubscriptionfields.model.{FieldsDefinitionRequest, JsonFormatters}
import uk.gov.hmrc.apisubscriptionfields.service.FieldsDefinitionService
import uk.gov.hmrc.play.test.UnitSpec
import util.FieldsDefinitionTestData

import scala.concurrent.Future

class FieldsDefinitionControllerSpec extends UnitSpec with FieldsDefinitionTestData with MockFactory with JsonFormatters {

  private val mockFieldsDefinitionService = mock[FieldsDefinitionService]
  private val controller = new FieldsDefinitionController(mockFieldsDefinitionService)

  "GET /definition/context/:apiContext/version/:apiVersion" should {
    "return CREATED when created in the repo" in {
      (mockFieldsDefinitionService.upsert _).expects(FakeFieldsDefinitionIdentifier, FakeFieldsDefinitions).returns(Future.successful(true))

      val json = mkJson(FieldsDefinitionRequest(FakeFieldsDefinitions))
      testSubmitResult(mkRequest(json)) { result =>
        status(result) shouldBe CREATED
      }
    }

    "return OK when updated in the repo" in {
      (mockFieldsDefinitionService.upsert _).expects(FakeFieldsDefinitionIdentifier, FakeFieldsDefinitions).returns(Future.successful(false))

      val json = mkJson(FieldsDefinitionRequest(FakeFieldsDefinitions))
      testSubmitResult(mkRequest(json)) { result =>
        status(result) shouldBe OK
      }
    }
  }

  private def testSubmitResult(request: Request[JsValue])(test: Future[Result] => Unit) {
    val action: Action[JsValue] = controller.upsertFieldsDefinition(fakeContext, fakeVersion)
    val result: Future[Result] = action.apply(request)
    test(result)
  }

  private def mkRequest(jsonBody: JsValue): Request[JsValue] =
    FakeRequest()
      .withJsonBody(jsonBody).map(r => r.json)

  private def mkJson(model: FieldsDefinitionRequest) = Json.toJson(model)(Json.writes[FieldsDefinitionRequest])

}
