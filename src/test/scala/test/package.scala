package com.gu.contentapi

import play.api.libs.ws.WS

package object sanity {

  def request(uri: String) = WS.url(uri).withRequestTimeout(1000)

}
