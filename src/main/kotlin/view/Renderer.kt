package view

import com.hubspot.jinjava.Jinjava
import java.io.File

class Renderer {
    companion object{
        private val jinjava = Jinjava()
        fun render(path: String, context: Map<String, Any> = emptyMap() ): String? {
            return jinjava.render(readTemplateFile(path), context)
        }

        private fun readTemplateFile(filePath: String): String? {
            return try {
                println(File(filePath).readText())
                File(filePath).readText()
            } catch (ex: Exception) {
                ex.printStackTrace()
                null
            }
        }
    }
}
