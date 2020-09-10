/*
 * Copyright 2020 HM Revenue & Customs
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

package uk.gov.hmrc.hmrcfrontend.views.components

import org.jsoup.Jsoup
import org.scalatest.{Matchers, WordSpec}
import play.api.i18n.Lang
import play.api.inject.guice.GuiceApplicationBuilder
import play.api.test.FakeRequest
import play.api.test.Helpers.contentAsString
import play.api.test.Helpers._
import uk.gov.hmrc.hmrcfrontend.views.JsoupHelpers
import uk.gov.hmrc.hmrcfrontend.views.html.components._

import scala.collection.immutable.List
import scala.collection.JavaConverters._
import java.util.{List => JavaList}

import org.scalatestplus.play.guice.GuiceOneAppPerSuite
import play.api.{Application, Play}
import uk.gov.hmrc.hmrcfrontend.MessagesSupport

class hmrcFooterSpec extends WordSpec with Matchers with JsoupHelpers with MessagesSupport with GuiceOneAppPerSuite {

  override def fakeApplication(): Application =
    new GuiceApplicationBuilder().configure(Map("play.allowGlobalApplication" -> "true")).build()

  def buildApp(properties: Map[String, String] = Map.empty): Application =
    new GuiceApplicationBuilder()
      .configure(Map("play.i18n.langs" -> List("en", "cy")) ++ properties)
      .build()

  implicit val fakeRequest = FakeRequest("GET", "/foo")

  val englishLinkTextEntries: JavaList[String] = List(
    "Cookies",
    "Privacy policy",
    "Terms and conditions",
    "Help using GOV.UK",
    "Open Government Licence v3.0",
    "© Crown copyright"
  ).asJava

  val welshLinkTextEntries: JavaList[String] = List(
    "Cwcis",
    "Polisi preifatrwydd",
    "Telerau ac Amodau",
    "Help wrth ddefnyddio GOV.UK",
    "Open Government Licence v3.0",
    "© Crown copyright"
  ).asJava

  "HmrcFooterLinks" should {
    "generate the correct list of links" in {
      implicit val app = buildApp()

      val content  = contentAsString(HmrcFooter()(messages, fakeRequest))
      val document = Jsoup.parse(content)
      val links    = document.getElementsByTag("a")

      links.eachText() should be(englishLinkTextEntries)
    }

    "generate the correct list of links in Welsh" in {
      implicit val app  = buildApp()
      val welshMessages = messagesApi.preferred(Seq(Lang("cy")))

      val content  = contentAsString(HmrcFooter()(welshMessages, fakeRequest))
      val document = Jsoup.parse(content)
      val links    = document.getElementsByTag("a")

      links.eachText() should be(welshLinkTextEntries)
    }
  }
}