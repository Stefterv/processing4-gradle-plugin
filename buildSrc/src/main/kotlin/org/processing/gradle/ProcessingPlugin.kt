package org.processing.gradle

import org.gradle.api.Action
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.api.artifacts.DependencySet
import org.gradle.api.file.SourceDirectorySet
import org.gradle.api.internal.file.DefaultSourceDirectorySet
import org.gradle.api.internal.tasks.DefaultSourceSet
import org.gradle.api.internal.tasks.TaskDependencyFactory
import org.gradle.api.model.ObjectFactory
import org.gradle.api.plugins.JavaLibraryPlugin
import org.gradle.api.plugins.JavaPluginExtension
import java.io.File
import javax.inject.Inject


interface PDESourceDirectorySet : SourceDirectorySet {
    companion object {
        /**
         * Name of the source set extension contributed by the antlr plugin.
         *
         * @since 8.0
         */
        const val NAME: String = "antlr"
    }
}

abstract class DefaultPDESourceDirectorySet @Inject constructor(
    sourceDirectorySet: SourceDirectorySet?,
    taskDependencyFactory: TaskDependencyFactory?
) : DefaultSourceDirectorySet(sourceDirectorySet, taskDependencyFactory), PDESourceDirectorySet


private fun createPDESourceDirectorySet(
    parentDisplayName: String,
    objectFactory: ObjectFactory
): PDESourceDirectorySet {
    val name = "$parentDisplayName.pde"
    val displayName = "$parentDisplayName Processing Source"
    val antlrSourceSet: PDESourceDirectorySet = objectFactory.newInstance(
        DefaultPDESourceDirectorySet::class.java, objectFactory.sourceDirectorySet(name, displayName)
    )
    antlrSourceSet.filter.include("**/*.pde")
    return antlrSourceSet
}
class ProcessingPlugin @Inject constructor(private val objectFactory: ObjectFactory) : Plugin<Project> {

    override fun apply(project: Project) {
        project.extensions.create("pde", ProcessingExtension::class.java)
        project.plugins.apply(JavaLibraryPlugin::class.java)
        project.dependencies.add("implementation", "com.github.micycle1:processing-core-4:4.3")

        project.repositories.add(project.repositories.maven { it.setUrl("https://jitpack.io") })
        project.repositories.add(project.repositories.maven { it.setUrl("https://jogamp.org/deployment/maven") })


        project.extensions.getByType(JavaPluginExtension::class.java).sourceSets.all { sourceSet ->
            // for each source set we will:
            // 1) Add a new 'antlr' virtual directory mapping
            val pdeSourceSet = createPDESourceDirectorySet(
                (sourceSet as DefaultSourceSet).displayName,
                objectFactory
            )
            sourceSet.getExtensions().add(PDESourceDirectorySet::class.java, PDESourceDirectorySet.NAME, pdeSourceSet)
            val srcDir = "src/" + sourceSet.getName() + "/pde"
            pdeSourceSet.srcDir(srcDir)
            sourceSet.getAllSource().source(pdeSourceSet)

            // 2) create an AntlrTask for this sourceSet following the gradle
            //    naming conventions via call to sourceSet.getTaskName()
            val taskName = sourceSet.getTaskName("generate", "GrammarSource")

            // 3) Set up the Antlr output directory (adding to javac inputs!)
            val outputDirectoryName = project.buildDir.toString() + "/generated-src/pde/" + sourceSet.getName()
            val outputDirectory = File(outputDirectoryName)
            sourceSet.getJava().srcDir(outputDirectory)

            project.tasks.register(
                taskName,
                ProcessingTask::class.java
            ) { antlrTask ->
                println("Processes the " + sourceSet.getName() + " PDE Files")
                antlrTask.description = "Processes the " + sourceSet.getName() + " Antlr grammars."
                // 4) set up convention mapping for default sources (allows user to not have to specify)
                antlrTask.source = pdeSourceSet
                antlrTask.outputDirectory = outputDirectory
            }

            // 5) register fact that antlr should be run before compiling
            project.tasks.named(
                sourceSet.getCompileJavaTaskName()
            ) { task -> task.dependsOn(taskName) }
        }
    }
}
