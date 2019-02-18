package org.koin.core

import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Assert.fail
import org.junit.Test
import org.koin.core.error.NoScopeDefinitionFoundException
import org.koin.core.error.ScopeAlreadyCreatedException
import org.koin.core.scope.ScopeCallback
import org.koin.dsl.koinApplication
import org.koin.dsl.module

class ScopeAPITest {

    @Test
    fun `create a scope instance`() {
        val koin = koinApplication { }.koin

        val scopeId = "myScope"
        val scope1 = koin.createScope(scopeId)
        val scope2 = koin.getScope(scopeId)

        assertEquals(scope1, scope2)
    }

    @Test
    fun `can't find a non created scope instance`() {
        val koin = koinApplication { }.koin

        val scopeId = "myScope"
        try {
            koin.getScope(scopeId)
            fail()
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    @Test
    fun `create different scopes`() {
        val koin = koinApplication { }.koin

        val scope1 = koin.createScope("myScope1")
        val scope2 = koin.createScope("myScope2")

        assertNotEquals(scope1, scope2)
    }

    @Test
    fun `can't create scope instance with unknown scope def`() {
        val koin = koinApplication { }.koin

        try {
            koin.createScope("myScope", "a_scope")
            fail()
        } catch (e: NoScopeDefinitionFoundException) {
            e.printStackTrace()
        }
    }

    @Test
    fun `create scope instance with scope def`() {
        val scopeName = "A_SCOPE"
        val koin = koinApplication {
            modules(
                module {
                    scope(scopeName){ }
                }
            )
        }.koin

        assertNotNull(koin.createScope("myScope", scopeName))
    }

    @Test
    fun `can't create a new scope if not closed`() {
        val koin = koinApplication { }.koin

        koin.createScope("myScope1")
        try {
            koin.createScope("myScope1")
            fail()
        } catch (e: ScopeAlreadyCreatedException) {
            e.printStackTrace()
        }
    }

    @Test
    fun `can't get a closed scope`() {
        val koin = koinApplication { }.koin

        val scope = koin.createScope("myScope1")
        scope.close()
        try {
            koin.getScope("myScope1")
            fail()
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    @Test
    fun `find a scope by id`() {
        val koin = koinApplication { }.koin

        val scopeId = "myScope"
        val scope1 = koin.createScope(scopeId)
        assertEquals(scope1, koin.getScope(scope1.id))
    }

    @Test
    fun `scope callback`() {
        val koin = koinApplication { }.koin

        val scopeId = "myScope"
        val scope1 = koin.createScope(scopeId)
        var closed = false
        scope1.registerCallback(object :ScopeCallback{
            override fun onClose() {
                closed = true
            }
        })
        scope1.close()
        assertTrue(closed)
    }
}