package com.otaliastudios.tools.publisher

import org.gradle.api.Project
import org.gradle.api.logging.LogLevel
import java.io.FileInputStream
import java.util.*

internal abstract class PublicationHandler {

    abstract fun applyPlugins(target: Project)

    abstract fun ownsPublication(name: String): Boolean

    abstract fun createPublication(name: String): Publication

    abstract fun fillPublication(target: Project, publication: Publication)

    abstract fun checkPublication(target: Project, publication: Publication)

    abstract fun createPublicationTasks(target: Project, publication: Publication, mavenPublication: String): Iterable<String>

    @Suppress("SameParameterValue")
    protected fun checkPublicationField(target: Project, value: Any?, field: String, fatal: Boolean) {
        if (value == null) {
            val message = "publisher.$field is not set."
            if (fatal) {
                throw IllegalArgumentException(message)
            } else {
                target.logger.log(LogLevel.WARN, message)
            }
        }
    }

    private var localProperties: Properties? = null

    protected fun findSecret(target: Project, key: String): String? {
        // Try with environmental variable.
        val env: String? = System.getenv(key)
        if (!env.isNullOrEmpty()) return env
        // Try with findProperty.
        val project = target.findProperty(key) as? String
        if (!project.isNullOrEmpty()) return project
        // Try with local.properties file.
        if (localProperties == null) {
            val properties = Properties()
            val file = target.rootProject.file("local.properties")
            if (file.exists()) {
                val stream = FileInputStream(file)
                properties.load(stream)
            }
            localProperties = properties
        }
        val local = localProperties!!.getProperty(key)
        if (!local.isNullOrEmpty()) return local
        // We failed. Return null.
        return null
    }
}