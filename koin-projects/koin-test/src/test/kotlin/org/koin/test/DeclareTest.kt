package org.koin.test

import org.junit.Assert.fail
import org.junit.Test
import org.koin.core.context.startKoin
import org.koin.core.logger.Level
import org.koin.test.mock.declare

class DeclareTest : KoinTest {

    @Test
    fun `declare on the fly with KoinTest`() {
        startKoin {
            logger(Level.DEBUG)
        }

        try {
            get<Simple.ComponentA>()
            fail()
        } catch (e: Exception) {
        }

        declare {
            single { Simple.ComponentA() }
        }

        get<Simple.ComponentA>()
    }
}