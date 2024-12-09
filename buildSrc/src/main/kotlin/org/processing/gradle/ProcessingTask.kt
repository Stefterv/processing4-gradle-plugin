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
    /**
     * Specifies that all rules call `traceIn`/`traceOut`.
     */
    @get:Input
    var isTrace: Boolean = false

    /**
     * Specifies that all lexer rules call `traceIn`/`traceOut`.
     */
    @get:Input
    var isTraceLexer: Boolean = false

    /**
     * Specifies that all parser rules call `traceIn`/`traceOut`.
     */
    @get:Input
    var isTraceParser: Boolean = false

    /**
     * Specifies that all tree walker rules call `traceIn`/`traceOut`.
     */
    @get:Input
    var isTraceTreeWalker: Boolean = false

    /**
     * List of command-line arguments passed to the antlr process
     *
     * @return The antlr command-line arguments
     */
    @get:Input
    var arguments: List<String> = ArrayList()
        set(arguments) {
            if (arguments != null) {
                field = arguments
            }
        }

    /**
     * Returns the classpath containing the Ant ANTLR task implementation.
     *
     * @return The Ant task implementation classpath.
     */
    /**
     * Specifies the classpath containing the Ant ANTLR task implementation.
     *
     * @param antlrClasspath The Ant task implementation classpath. Must not be null.
     */
//    @get:Classpath
//    var classpath: FileCollection? = null
//        /**
//         * Specifies the classpath containing the Ant ANTLR task implementation.
//         *
//         * @param antlrClasspath The Ant task implementation classpath. Must not be null.
//         */
//        protected set

    /**
     * Returns the directory to generate the parser source files into.
     *
     * @return The output directory.
     */
    /**
     * Specifies the directory to generate the parser source files into.
     *
     * @param outputDirectory The output directory. Must not be null.
     */
    @get:OutputDirectory
    var outputDirectory: File? = null

    /**
     * The maximum heap size for the forked antlr process (ex: '1g').
     */
    @get:Internal
    var maxHeapSize: String? = null
    private var sourceSetDirectories: FileCollection? = null

    /**
     * The sources for incremental change detection.
     *
     * @since 6.0
     */
    @get:InputFiles
    @get:PathSensitive(PathSensitivity.RELATIVE)
    @get:IgnoreEmptyDirectories
    @get:SkipWhenEmpty
    open val stableSources: FileCollection = project.files(Callable<Any> { this.getSource() })


    @get:Inject
    open val workerProcessBuilderFactory: WorkerProcessFactory
        get() {
            throw UnsupportedOperationException()
        }

    @get:Inject
    open val projectLayout: ProjectLayout
        get() {
            throw UnsupportedOperationException()
        }

    /**
     * Generate the parsers.
     *
     * @since 6.0
     */
    @TaskAction
    fun execute(inputChanges: InputChanges) {
        val grammarFiles: MutableSet<File> = HashSet()
        val stableSources = stableSources
        if (inputChanges.isIncremental) {
            var rebuildRequired = false
            for (fileChange: FileChange in inputChanges.getFileChanges(stableSources)) {
                if (fileChange.fileType == FileType.FILE) {
                    if (fileChange.changeType == ChangeType.REMOVED) {
                        rebuildRequired = true
                        break
                    }
                    grammarFiles.add(fileChange.file)
                }
            }
            if (rebuildRequired) {
                try {
                    deleter.ensureEmptyDirectory(outputDirectory)
                } catch (ex: IOException) {
                    throw UncheckedIOException(ex)
                }
                grammarFiles.addAll(stableSources.files)
            }
        } else {
            grammarFiles.addAll(stableSources.files)
        }

        for (file in grammarFiles) {
            println("Processing file: $file")

            var name = file.nameWithoutExtension.capitalized()

            var preprocessor = PdePreprocessor.builderFor(name).build();

            // load string from file
            var code = file.readText()

            var writer = StringWriter();

            preprocessor.write(writer, code);

            var result = writer.toString()

            println("Result: $result")

            File(outputDirectory, name+".java").writeText(result)
        }
    }

    private fun projectDir(): File {
        return projectLayout.projectDirectory.asFile
    }

    /**
     * Sets the source for this task. Delegates to [.setSource].
     *
     * If the source is of type [SourceDirectorySet], then the relative path of each source grammar files
     * is used to determine the relative output path of the generated source
     * If the source is not of type [SourceDirectorySet], then the generated source files end up
     * flattened in the specified output directory.
     *
     * @param source The source.
     * @since 4.0
     */
    override fun setSource(source: FileTree) {
        setSource(source as Any)
    }

    /**
     * Sets the source for this task. Delegates to [SourceTask.setSource].
     *
     * If the source is of type [SourceDirectorySet], then the relative path of each source grammar files
     * is used to determine the relative output path of the generated source
     * If the source is not of type [SourceDirectorySet], then the generated source files end up
     * flattened in the specified output directory.
     *
     * @param source The source.
     */
    override fun setSource(source: Any) {
        super.setSource(source)
        if (source is SourceDirectorySet) {
            this.sourceSetDirectories = source.sourceDirectories
        }
    }

    /**
     * {@inheritDoc}
     */
    @Internal("tracked via stableSources")
    override fun getSource(): FileTree {
        return super.getSource()
    }

    @get:Inject
    open val deleter: Deleter
        get() {
            throw UnsupportedOperationException("Decorator takes care of injection")
        }
}