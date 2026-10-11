package com.example.onestopfootball.data.auth

import android.os.Build
import android.util.Base64
import java.security.MessageDigest
import java.security.SecureRandom
import javax.crypto.SecretKeyFactory
import javax.crypto.spec.PBEKeySpec

data class PasswordHash(
    val hash: String,
    val salt: String,
    val algorithm: String,
    val iterations: Int
)

object PasswordHasher {

    private const val ALGORITHM = "PBKDF2WithHmacSHA256"

    private const val ITERATIONS = 600_000

    private const val KEY_LENGTH_BITS = 256

    fun isSupported(): Boolean {
        return Build.VERSION.SDK_INT >= Build.VERSION_CODES.O
    }

    fun createHash(password: String): PasswordHash {

        check(isSupported()) {
            "Password hashing requires Android 8.0 or newer."
        }

        val saltBytes = ByteArray(16)
        SecureRandom().nextBytes(saltBytes)

        val hashBytes = derive(
            password = password,
            salt = saltBytes,
            iterations = ITERATIONS
        )

        return PasswordHash(
            hash = Base64.encodeToString(
                hashBytes,
                Base64.NO_WRAP
            ),
            salt = Base64.encodeToString(
                saltBytes,
                Base64.NO_WRAP
            ),
            algorithm = ALGORITHM,
            iterations = ITERATIONS
        )
    }

    fun verify(
        password: String,
        encodedSalt: String,
        encodedHash: String,
        algorithm: String,
        iterations: Int
    ): Boolean {

        if (!isSupported()) return false

        return try {

            if (algorithm != ALGORITHM) return false
            if (iterations <= 0) return false

            val saltBytes = Base64.decode(
                encodedSalt,
                Base64.NO_WRAP
            )

            val expectedHash = Base64.decode(
                encodedHash,
                Base64.NO_WRAP
            )

            val actualHash = derive(
                password = password,
                salt = saltBytes,
                iterations = iterations
            )

            MessageDigest.isEqual(
                expectedHash,
                actualHash
            )

        } catch (_: Exception) {
            false
        }
    }

    private fun derive(
        password: String,
        salt: ByteArray,
        iterations: Int
    ): ByteArray {

        val passwordChars = password.toCharArray()

        val spec = PBEKeySpec(
            passwordChars,
            salt,
            iterations,
            KEY_LENGTH_BITS
        )

        return try {

            SecretKeyFactory
                .getInstance(ALGORITHM)
                .generateSecret(spec)
                .encoded

        } finally {

            spec.clearPassword()
            passwordChars.fill('\u0000')
        }
    }
}