package com.vitorpamplona.amethyst.service.model

import java.util.Date
import nostr.postr.Utils
import nostr.postr.events.Event
import nostr.postr.events.MetadataEvent

class ChannelCreateEvent (
  id: ByteArray,
  pubKey: ByteArray,
  createdAt: Long,
  tags: List<List<String>>,
  content: String,
  sig: ByteArray
): Event(id, pubKey, createdAt, kind, tags, content, sig) {
  @Transient val channelInfo: ChannelData

  init {
    try {
      channelInfo = MetadataEvent.gson.fromJson(content, ChannelData::class.java)
    } catch (e: Exception) {
      throw Error("can't parse $content", e)
    }
  }

  companion object {
    const val kind = 40

    fun create(channelInfo: ChannelData?, privateKey: ByteArray, createdAt: Long = Date().time / 1000): ChannelCreateEvent {
      val content = if (channelInfo != null)
        gson.toJson(channelInfo)
      else
        ""

      val pubKey = Utils.pubkeyCreate(privateKey)
      val tags = emptyList<List<String>>()
      val id = generateId(pubKey, createdAt, kind, tags, content)
      val sig = Utils.sign(id, privateKey)
      return ChannelCreateEvent(id, pubKey, createdAt, tags, content, sig)
    }
  }

  data class ChannelData(var name: String?, var about: String?, var picture: String?)
}