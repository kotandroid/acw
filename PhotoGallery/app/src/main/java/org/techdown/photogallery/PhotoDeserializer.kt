package org.techdown.photogallery

import android.util.Log
import com.google.gson.JsonDeserializationContext
import com.google.gson.JsonDeserializer
import com.google.gson.JsonElement
import org.techdown.photogallery.api.PhotoResponse
import java.lang.reflect.Type

class PhotoDeserializer : JsonDeserializer<PhotoResponse> {

    override fun deserialize(
        json: JsonElement?,
        typeOfT: Type?,
        context: JsonDeserializationContext?
    ): PhotoResponse {
        // Json Element객체를 가져오고 Photo Response 객체로 변환
        val jsonObject =
            json?.asJsonObject ?: throw NullPointerException("Response Json String is null")
        val photoArr= jsonObject.get("photos").asJsonObject.get("photo").asJsonArray
        Log.d("PhotoDeserializer",photoArr.toString())
        val listOfGallery = photoArr.map {
            val photo = it.asJsonObject
            Log.d("PhotoDeserializer",photo["url_s"].asString)

            GalleryItem(photo["title"].asString, photo["id"].asString, photo["url_s"].asString,photo["owner"].asString)
        }

        return PhotoResponse().apply { galleryItems =listOfGallery}

    }
}
