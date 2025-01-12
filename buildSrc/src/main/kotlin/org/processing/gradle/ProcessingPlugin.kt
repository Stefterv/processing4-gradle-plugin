package org.processing.gradle

import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.api.file.SourceDirectorySet
import org.gradle.api.internal.file.DefaultSourceDirectorySet
import org.gradle.api.internal.tasks.TaskDependencyFactory
import org.gradle.api.model.ObjectFactory
import org.gradle.api.plugins.JavaPlugin
import org.gradle.api.plugins.JavaPluginExtension
import org.jetbrains.compose.ComposeExtension
import org.jetbrains.compose.desktop.DesktopExtension
import javax.inject.Inject

class ProcessingPlugin @Inject constructor(private val objectFactory: ObjectFactory) : Plugin<Project> {
    override fun apply(project: Project) {
        project.plugins.apply(JavaPlugin::class.java)

        project.plugins.apply("org.jetbrains.compose")
        project.plugins.apply("org.jetbrains.kotlin.jvm")

        project.dependencies.add("implementation", "org.processing:core:4.3.1")
        project.dependencies.add("implementation", project.fileTree("src").apply { include("**/code/*.jar") })

        project.repositories.add(project.repositories.maven { it.setUrl("https://jogamp.org/deployment/maven") })
        project.repositories.add(project.repositories.mavenCentral())

        //TODO: Find the name automatically
        project.extensions.configure(ComposeExtension::class.java) { extension ->
            extension.extensions.getByType(DesktopExtension::class.java).application { application ->
                application.mainClass = "brightness"
                application.nativeDistributions.modules("java.management")
            }
        }

        project.tasks.create("sketch").apply {
            group = "processing"
            description = "Runs the Processing sketch"
            dependsOn("run")
        }
        project.tasks.create("present").apply {
            // TODO: Implement dynamic fullscreen by setting the properties
            group = "processing"
            description = "Presents the Processing sketch"
            dependsOn("run")
        }
        project.tasks.create("export").apply {
            group = "processing"
            description = "Creates a distributable version of the Processing sketch"
            dependsOn("createDistributable")
        }

        project.extensions.getByType(JavaPluginExtension::class.java).sourceSets.all { sourceSet ->

            // TODO Look for .pde files in the source directory instead
            val pdeSourceSet = objectFactory.newInstance(
                DefaultPDESourceDirectorySet::class.java,
                objectFactory.sourceDirectorySet("${sourceSet.name}.pde", "${sourceSet.name} Processing Source")
            ).apply {
                filter.include("**/*.pde")
                srcDir("src/${sourceSet.name}/pde")
            }
            sourceSet.allSource.source(pdeSourceSet)

            val outputDirectory = project.layout.buildDirectory.file( "generated/pde/" + sourceSet.name).get().asFile
            sourceSet.java.srcDir(outputDirectory)

            // TODO: Support imported libraries
            // TODO: Merge all pde files into one
            // TODO: Support multiple sketches?

            val taskName = sourceSet.getTaskName("preprocess", "PDE")
            project.tasks.register(taskName, ProcessingTask::class.java) { task ->
                task.description = "Processes the ${sourceSet.name} PDE"
                task.source = pdeSourceSet
                task.outputDirectory = outputDirectory
            }

            project.tasks.named(
                sourceSet.compileJavaTaskName
            ) { task -> task.dependsOn(taskName) }
        }
    }
    abstract class DefaultPDESourceDirectorySet @Inject constructor(
        sourceDirectorySet: SourceDirectorySet,
        taskDependencyFactory: TaskDependencyFactory
    ) : DefaultSourceDirectorySet(sourceDirectorySet, taskDependencyFactory), SourceDirectorySet
}

