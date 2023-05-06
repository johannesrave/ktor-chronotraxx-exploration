package support

import kotlinx.serialization.decodeFromString
import kotlinx.serialization.json.Json
import java.io.File
import java.nio.file.Files
import java.nio.file.Paths
import java.util.stream.Stream

class JsonFileReader(
    val jsonFormat: Json = Json { prettyPrint = true }
) {
    inline fun <reified Model> parseJsonFile(filePath: String): Model? {
        println("""parsing ${Model::class.simpleName} in $filePath""")
        return readTextFile(filePath)?.let { jsonFormat.decodeFromString<Model>(it) }
    }

    fun readTextFile(filePath: String): String? {
        return try {
            File(filePath).readText()
        } catch (ex: Exception) {
            ex.printStackTrace()
            null
        }
    }
}

fun streamFilePathsInParallelFrom(directory: String, ending: String = ""): Stream<String> {
    val projectDirAbsolutePath = Paths.get("").toAbsolutePath().toString()
    val resourcesPath = Paths.get(projectDirAbsolutePath, "data/input", directory)

    return Files.walk(resourcesPath)
        .parallel()
        .filter { item -> Files.isRegularFile(item) && item.toString().endsWith(ending) }
        .map { filePath -> filePath.toString() }
}