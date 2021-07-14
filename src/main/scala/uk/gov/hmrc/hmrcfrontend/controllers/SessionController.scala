/*
 * Copyright 2021 HM Revenue & Customs
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

package uk.gov.hmrc.hmrcfrontend.controllers

import play.api.http.MimeTypes
import play.api.mvc._
import uk.gov.hmrc.hmrcfrontend.config.TimeoutDialogConfig

import java.time.temporal.ChronoUnit
import javax.inject.{Inject, Singleton}
import java.time.{Duration, Instant}

@Singleton
class SessionController @Inject() (cc: ControllerComponents, config: TimeoutDialogConfig)
    extends AbstractController(cc) {
  def session: Action[AnyContent] = Action { implicit request =>
    // FIXME: this should use SessionKeys from bootstrap-play
    // FIXME: duplication of knowledge from the SessionTimeoutFilter in bootstrap-play,
    // Could the calculation should be done in bootstrap-play and added as it's own session key?
    val ts           = request.session.get("ts")
    val maybeSeconds = ts
      .flatMap(timestampToInstant)
      .map { (ts: Instant) =>
        val expiry = ts.plusSeconds(config.timeoutInSeconds)

        Instant.now().until(expiry, ChronoUnit.SECONDS).toString
      }
      .getOrElse("")

    Ok(s"""{ "secondsRemaining": $maybeSeconds }""").as(MimeTypes.JSON);
  }

  private def timestampToInstant(timestampMs: String): Option[Instant] =
    try Some(Instant.ofEpochMilli(timestampMs.toLong))
    catch {
      case e: NumberFormatException => None
    }
}
