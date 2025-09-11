package ru.jerael.booktracker.android.domain.model.book

import kotlinx.coroutines.test.runTest
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.assertNull
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.Arguments
import org.junit.jupiter.params.provider.MethodSource
import org.junit.jupiter.params.provider.ValueSource
import java.util.stream.Stream


class BookStatusTest {

    companion object {

        @JvmStatic
        private fun provideBookStatusValidStrings(): Stream<Arguments> = Stream.of(
            Arguments.of("read", BookStatus.READ),
            Arguments.of("Read", BookStatus.READ),
            Arguments.of("READ", BookStatus.READ),
            Arguments.of("want_to_read", BookStatus.WANT_TO_READ),
            Arguments.of("Want_To_Read", BookStatus.WANT_TO_READ),
            Arguments.of("WANT_TO_READ", BookStatus.WANT_TO_READ),
            Arguments.of("reading", BookStatus.READING),
            Arguments.of("Reading", BookStatus.READING),
            Arguments.of("READING", BookStatus.READING)
        )
    }

    @ParameterizedTest(name = "[{index}] Input: \"{0}\" => Expected: {1}")
    @MethodSource("provideBookStatusValidStrings")
    fun `when valid string is provided, then return corresponding BookStatus`(
        inputString: String,
        expectedStatus: BookStatus
    ) = runTest {
        val actualStatus = BookStatus.fromString(inputString)

        assertEquals(expectedStatus, actualStatus)
    }

    @ParameterizedTest(name = "[{index}] Input: \"{0}\" => Expected: null")
    @ValueSource(strings = ["", " ", "unknown_status", "want to read"])
    fun `when invalid string is provided, then return null`(inputString: String) = runTest {
        val actualStatus = BookStatus.fromString(inputString)

        assertNull(actualStatus)
    }
}