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

import play.api.Logger
import reactivemongo.api.commands.WriteResult

trait MongoErrorHandler {

  def handleDeleteError(result: WriteResult, exceptionMsg: => String): Boolean = {
    handleError(result, databaseAltered, exceptionMsg)
  }

  def handleUpsertError(result: WriteResult, exceptionMsg: => String, isInserted: Boolean): Boolean = {

    def handleUpsertError(result: WriteResult) =
      if (databaseAltered(result))
        isInserted
      else
        throw new RuntimeException(exceptionMsg)

    handleError(result, handleUpsertError, exceptionMsg)
  }

  def handleError(result: WriteResult, f: WriteResult => Boolean, exceptionMsg: String): Boolean = {
    result.writeConcernError.fold(f(result)) {
      errMsg => {
        val errorMsg = s"""$exceptionMsg. $errMsg"""
        Logger.error(errorMsg)
        throw new RuntimeException(errorMsg)
      }
    }
  }

  def databaseAltered(writeResult: WriteResult): Boolean = writeResult.n > 0
}
