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

package acceptance

import org.scalatest.OptionValues
import play.api.Logger
import play.api.mvc._
import play.api.test.Helpers._
import uk.gov.hmrc.apisubscriptionfields.model.ErrorCode.{FIELDS_DEFINITION_ID_NOT_FOUND, SUBSCRIPTION_FIELDS_ID_NOT_FOUND}
import uk.gov.hmrc.apisubscriptionfields.model.JsErrorResponse
import util.SubscriptionFieldsTestData

import scala.concurrent.Future

class ApiSubscriptionFieldsUnhappySpec extends AcceptanceTestSpec
  with OptionValues
  with SubscriptionFieldsTestData {


  feature("Subscription-Fields") {
    Logger.logger.info(s"App.mode = ${app.mode.toString}")

    scenario("the API is called to GET an unknown subscription identifier") {

      Given("ValidGetRequest.copyFakeRequest(method = GET, uri = endpoint(fakeAppId, fakeContext, fakeVersion))a request with an unknown identifier")
      val request = ValidGetRequest.copyFakeRequest(method = GET, uri = idEndpoint(fakeAppId, fakeContext, fakeVersion))

      When("a GET request with data is sent to the API")
      val result: Option[Future[Result]] = route(app, request)

      Then(s"a response with a 404 status is received")
      result shouldBe 'defined
      val resultFuture = result.value

      status(resultFuture) shouldBe NOT_FOUND

      And("the response body is empty")
      contentAsJson(resultFuture) shouldBe JsErrorResponse(SUBSCRIPTION_FIELDS_ID_NOT_FOUND, s"Subscription Fields were not found")
    }

    scenario("the API is called to DELETE an unknown subscription fields identifier") {

      Given("a request with an unknown identifier")
      val request = ValidDeleteRequest.copyFakeRequest(method = DELETE, uri = idEndpoint(fakeAppId, fakeContext, fakeVersion))

      When("a GET request with data is sent to the API")
      val result: Option[Future[Result]] = route(app, request)

      Then(s"a response with a 404 status is received")
      result shouldBe 'defined
      val resultFuture = result.value

      status(resultFuture) shouldBe NOT_FOUND

      And("the response body is empty")
      contentAsJson(resultFuture) shouldBe JsErrorResponse(SUBSCRIPTION_FIELDS_ID_NOT_FOUND, "Subscription Fields were not found")
    }

  }

  feature("Fields-Definition") {
    scenario("the API is called to GET an unknown fields definition") {

      Given("the API is called to GET an unknown fields definition")
      val request = ValidGetRequest.copyFakeRequest(method = GET, uri = definitionEndpoint(fakeContext, "unknown"))

      When("a GET request with data is sent to the API")
      val result: Option[Future[Result]] = route(app, request)

      Then(s"a response with a 404 status is received")
      result shouldBe 'defined
      val resultFuture = result.value

      status(resultFuture) shouldBe NOT_FOUND

      And("the response body is empty")
      contentAsJson(resultFuture) shouldBe JsErrorResponse(FIELDS_DEFINITION_ID_NOT_FOUND, "Fields definition was not found")
    }

  }
}
