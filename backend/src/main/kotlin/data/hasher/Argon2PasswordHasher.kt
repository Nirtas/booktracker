/*
 * BookTracker is a full-stack application for tracking your reading list.
 * Copyright (C) 2025  Jerael (https://github.com/Nirtas)
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as published
 * by the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Affero General Public License for more details.
 *
 * You should have received a copy of the GNU Affero General Public License
 * along with this program.  If not, see <https://www.gnu.org/licenses/>.
 */

package ru.jerael.booktracker.backend.data.hasher

import de.mkammerer.argon2.Argon2Factory
import de.mkammerer.argon2.Argon2Helper
import ru.jerael.booktracker.backend.domain.hasher.PasswordHasher

class Argon2PasswordHasher : PasswordHasher {
    private val saltLength = 16
    private val hashLength = 32
    private val argon2 = Argon2Factory.create(Argon2Factory.Argon2Types.ARGON2id, saltLength, hashLength)

    private val memory = 65536
    private val parallelism = 1
    private val iterations = Argon2Helper.findIterations(argon2, 1000, memory, parallelism)

    override fun hash(password: String): String {
        val charArray = password.toCharArray()
        try {
            return argon2.hash(iterations, memory, parallelism, charArray)
        } finally {
            argon2.wipeArray(charArray)
        }
    }

    override fun verify(password: String, hash: String): Boolean {
        val charArray = password.toCharArray()
        return try {
            argon2.verify(hash, charArray)
        } catch (e: Exception) {
            false
        } finally {
            argon2.wipeArray(charArray)
        }
    }
}