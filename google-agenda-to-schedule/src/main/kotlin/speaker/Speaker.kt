package speaker

import talk.Talk

data class Speaker(
  val id: String,
  var firstName: String = "",
  var lastName: String? = null,
  var imageURL: String? = null,
  var bio: String? = null,
  var tweetHandle: String? = null,
  var talks: MutableList<Talk> = mutableListOf())
