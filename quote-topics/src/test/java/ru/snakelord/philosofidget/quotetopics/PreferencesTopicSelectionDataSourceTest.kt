package ru.snakelord.philosofidget.quotetopics

import android.content.SharedPreferences
import java.lang.reflect.Proxy
import org.junit.Assert.*
import org.junit.Test
import ru.snakelord.philosofidget.quotetopics.data.PreferencesTopicSelectionDataSource

class PreferencesTopicSelectionDataSourceTest {
    private class PreferencesStub(initial: Map<String, String> = emptyMap()) {
        val values = initial.toMutableMap()
        val reads = mutableListOf<String>()
        var applyCount = 0
        var writeFailure: RuntimeException? = null
        private val pending = mutableMapOf<String, String>()

        private val editor = Proxy.newProxyInstance(
            SharedPreferences.Editor::class.java.classLoader,
            arrayOf(SharedPreferences.Editor::class.java)
        ) { proxy, method, args ->
            when (method.name) {
                "putString" -> {
                    writeFailure?.let { throw it }
                    pending[args!![0] as String] = args[1] as String
                    proxy
                }
                "apply" -> {
                    values.putAll(pending)
                    pending.clear()
                    applyCount++
                    null
                }
                else -> error("Unexpected editor method: ${method.name}")
            }
        } as SharedPreferences.Editor

        val preferences = Proxy.newProxyInstance(
            SharedPreferences::class.java.classLoader,
            arrayOf(SharedPreferences::class.java)
        ) { _, method, args ->
            when (method.name) {
                "getString" -> {
                    val key = args!![0] as String
                    reads.add(key)
                    values[key] ?: args[1]
                }
                "edit" -> editor
                else -> error("Unexpected preferences method: ${method.name}")
            }
        } as SharedPreferences
    }

    @Test fun `absent selection returns null without writing preferences`() {
        val stub = PreferencesStub()
        assertNull(PreferencesTopicSelectionDataSource(stub.preferences).getSelectedTopicId())
        assertEquals(listOf("selected_quote_topic"), stub.reads)
        assertEquals(0, stub.applyCount)
        assertTrue(stub.values.isEmpty())
    }

    @Test fun `existing selection reads established storage key`() {
        val stub = PreferencesStub(mapOf("selected_quote_topic" to "love", "other" to "life"))
        assertEquals("love", PreferencesTopicSelectionDataSource(stub.preferences).getSelectedTopicId())
        assertEquals(0, stub.applyCount)
    }

    @Test fun `selection write is applied and restored by a new data source`() {
        val stub = PreferencesStub(mapOf("other" to "untouched"))
        val source = PreferencesTopicSelectionDataSource(stub.preferences)
        source.setSelectedTopicId("wisdom")
        assertEquals(1, stub.applyCount)
        assertEquals("wisdom", stub.values["selected_quote_topic"])
        assertEquals("untouched", stub.values["other"])
        assertEquals("wisdom", PreferencesTopicSelectionDataSource(stub.preferences).getSelectedTopicId())
        source.setSelectedTopicId("peace")
        assertEquals(2, stub.applyCount)
        assertEquals("peace", source.getSelectedTopicId())
    }

    @Test fun `preference write failures propagate to caller without applying changes`() {
        val stub = PreferencesStub(mapOf("selected_quote_topic" to "life"))
        val failure = IllegalStateException("Preferences unavailable")
        stub.writeFailure = failure
        val source = PreferencesTopicSelectionDataSource(stub.preferences)
        assertSame(failure, assertThrows(IllegalStateException::class.java) {
            source.setSelectedTopicId("peace")
        })
        assertEquals("life", source.getSelectedTopicId())
        assertEquals(0, stub.applyCount)
    }
}
