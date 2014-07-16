package com.gu.contentapi

import play.api.libs.ws.WS

package object sanity {




  def request(uri: String) = WS.url(uri).withRequestTimeout(10000)

  def requestHost(path: String) =
  // make sure query string is included
    if(path.contains("?"))
    request(Config.host + path + "&api-key=" + Config.apiKey)
    else
    request(Config.host + path + "?api-key=" + Config.apiKey)
}
