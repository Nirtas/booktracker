package data.hasher

import org.junit.jupiter.api.Assertions.assertFalse
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test
import ru.jerael.booktracker.backend.data.hasher.Argon2PasswordHasher
import ru.jerael.booktracker.backend.domain.hasher.PasswordHasher

class Argon2PasswordHasherTest {

    private val passwordHasher: PasswordHasher = Argon2PasswordHasher()

    @Test
    fun `when password is hashed, verify with the correct password should return true`() {
        val password = "password A"

        val hash = passwordHasher.hash(password)
        val isVerified = passwordHasher.verify(password, hash)

        assertTrue(isVerified)
    }

    @Test
    fun `when password is hashed, verify with an incorrect password should return false`() {
        val correctPassword = "password A"
        val incorrectPassword = "password B"

        val hash = passwordHasher.hash(correctPassword)
        val isVerified = passwordHasher.verify(incorrectPassword, hash)

        assertFalse(isVerified)
    }

    @Test
    fun `when password is an empty string, verify should work correctly`() {
        val emptyPassword = ""

        val hash = passwordHasher.hash(emptyPassword)
        val isVerified = passwordHasher.verify(emptyPassword, hash)
        val isNotVerified = passwordHasher.verify("password A", hash)

        assertTrue(isVerified)
        assertFalse(isNotVerified)
    }

    @Test
    fun `when hash is invalid, verify should return false and not throw an exception`() {
        val password = "password A"
        val invalidHash = "invalid argon2 hash"

        val isVerified = passwordHasher.verify(password, invalidHash)

        assertFalse(isVerified)
    }

    @Test
    fun `when hash is from a different password, verify should return false`() {
        val passwordA = "password A"
        val passwordB = "password B"

        val hashA = passwordHasher.hash(passwordA)
        val isVerified = passwordHasher.verify(passwordB, hashA)

        assertFalse(isVerified)
    }

    @Test
    fun `when hashing the same password twice, it should produce different hashes`() {
        val password = "password A"

        val hash1 = passwordHasher.hash(password)
        val hash2 = passwordHasher.hash(password)

        assertTrue(hash1 != hash2)
        assertTrue(passwordHasher.verify(password, hash1))
        assertTrue(passwordHasher.verify(password, hash2))
    }
}