package com.senacor.postbook.network

import kotlinx.serialization.Serializable
import org.hamcrest.CoreMatchers.equalTo
import org.junit.Assert.assertThat
import org.junit.Test
import org.reflections.Reflections
import org.reflections.scanners.SubTypesScanner
import org.reflections.util.ClasspathHelper
import org.reflections.util.ConfigurationBuilder
import org.reflections.util.FilterBuilder

class SerializationTest {

    @Test
    fun allModelsHaveSerializable() {
        val packageName = "com.senacor.postbook.network.model"
        val r = Reflections(
            ConfigurationBuilder()
                .setUrls(ClasspathHelper.forPackage(packageName))
                .setScanners(SubTypesScanner(false))
                .filterInputsBy(FilterBuilder().includePackage(packageName))
        )
        val classes = r.getSubTypesOf(Any::class.java).filter { !it.name.contains("$") }

        classes.forEach {
            assertThat(it.isAnnotationPresent(Serializable::class.java), equalTo(true))
        }
    }
}