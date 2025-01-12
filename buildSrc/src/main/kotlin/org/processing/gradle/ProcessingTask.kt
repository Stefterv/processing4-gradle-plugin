package org.processing.gradle

import org.gradle.api.file.*
import org.gradle.api.tasks.*
import org.gradle.configurationcache.extensions.capitalized
import org.gradle.internal.file.Deleter
import org.gradle.process.internal.worker.WorkerProcessFactory
import org.gradle.work.ChangeType
import org.gradle.work.FileChange
import org.gradle.work.InputChanges
import processing.mode.java.preproc.PdePreprocessor
import java.io.File
import java.io.IOException
import java.io.StringWriter
import java.io.UncheckedIOException
import java.util.concurrent.Callable
import javax.inject.Inject
abstract class ProcessingTask() : SourceTask() {
     @get:OutputDirectory
    var outputDirectory: File? = null

    @get:InputFiles
    @get:PathSensitive(PathSensitivity.RELATIVE)
    @get:IgnoreEmptyDirectories
    @get:SkipWhenEmpty
    open val stableSources: FileCollection = project.files(Callable<Any> { this.source })

    @TaskAction
    fun execute(inputChanges: InputChanges) {
        val files: MutableSet<File> = HashSet()
        if (inputChanges.isIncremental) {
            var rebuildRequired = false
            for (fileChange: FileChange in inputChanges.getFileChanges(stableSources)) {
                if (fileChange.fileType == FileType.FILE) {
                    if (fileChange.changeType == ChangeType.REMOVED) {
                        rebuildRequired = true
                        break
                    }
                    files.add(fileChange.file)
                }
            }
            if (rebuildRequired) {
                try {
                    deleter.ensureEmptyDirectory(outputDirectory)
                } catch (ex: IOException) {
                    throw UncheckedIOException(ex)
                }
                files.addAll(stableSources.files)
            }
        } else {
            files.addAll(stableSources.files)
        }

        for (file in files) {
            val name = file.nameWithoutExtension.capitalized()
            val preprocessor = PdePreprocessor.builderFor(name).build();
            val code = file.readText()

            File(outputDirectory, "$name.java").bufferedWriter().use { out ->
                preprocessor.write(out, code)
            }
        }
    }

    @get:Inject
    open val deleter: Deleter
        get() {
            throw UnsupportedOperationException("Decorator takes care of injection")
        }
}