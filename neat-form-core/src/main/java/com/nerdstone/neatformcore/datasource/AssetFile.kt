package com.nerdstone.neatformcore.datasource

import android.content.Context
import android.os.Build
import com.nerdstone.neatformcore.domain.data.FileSource
import java.io.InputStream
import java.nio.charset.StandardCharsets

object AssetFile : FileSource {

    override fun readAssetFileAsString(context: Context, filePath: String): String {
        val inputStream = openFileAsset(context, filePath)
        val size = inputStream.available()
        val buffer = ByteArray(size)
        inputStream.read(buffer)
        inputStream.close()
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            String(buffer, StandardCharsets.UTF_8)
        } else {
            String(buffer)
        }
    }

    fun openFileAsset(context: Context, path: String): InputStream {
        return context.resources.assets.open(path)
    }

}
