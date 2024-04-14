package org.processing.gradle

import org.gradle.api.Project

open class ProcessingExtension(project: Project) {
    init {
        project.logger.lifecycle("ProcessingExtension created")
    }
}