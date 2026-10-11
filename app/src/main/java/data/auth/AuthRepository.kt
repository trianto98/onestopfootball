package com.example.onestopfootball.data.auth

import android.util.Patterns
import com.example.onestopfootball.data.local.UserDao
import com.example.onestopfootball.data.local.UserEntity
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.util.Locale

sealed class AuthResult {

    data class Success(
        val user: UserEntity
    ) : AuthResult()

    data class Failure(
        val message: String
    ) : AuthResult()
}

class AuthRepository(
    private val userDao: UserDao
) {

    suspend fun register(
        usernameInput: String,
        emailInput: String,
        password: String,
        confirmPassword: String
    ): AuthResult {

        val username = usernameInput.trim()
        val email = emailInput.trim()

        if (username.isBlank()) {
            return AuthResult.Failure(
                "Username cannot be empty."
            )
        }

        if (email.isBlank() ||
            !Patterns.EMAIL_ADDRESS.matcher(email).matches()
        ) {
            return AuthResult.Failure(
                "Please enter a valid email address."
            )
        }

        val passwordError = validatePassword(password)

        if (passwordError != null) {
            return AuthResult.Failure(passwordError)
        }

        if (password != confirmPassword) {
            return AuthResult.Failure(
                "Passwords do not match."
            )
        }

        if (!PasswordHasher.isSupported()) {
            return AuthResult.Failure(
                "Authentication requires Android 8.0 or newer."
            )
        }

        val usernameKey = username.lowercase(Locale.ROOT)
        val emailKey = email.lowercase(Locale.ROOT)

        if (userDao.findByUsername(usernameKey) != null) {
            return AuthResult.Failure(
                "Username is already registered."
            )
        }

        if (userDao.findByEmail(emailKey) != null) {
            return AuthResult.Failure(
                "Email is already registered."
            )
        }

        val hashed = withContext(Dispatchers.Default) {
            PasswordHasher.createHash(password)
        }

        val user = UserEntity(
            username = username,
            usernameKey = usernameKey,
            email = email,
            emailKey = emailKey,
            passwordHash = hashed.hash,
            passwordSalt = hashed.salt,
            passwordAlgorithm = hashed.algorithm,
            passwordIterations = hashed.iterations
        )

        val insertedId = userDao.insertUser(user)

        if (insertedId == -1L) {
            return AuthResult.Failure(
                "Username or email is already registered."
            )
        }

        return AuthResult.Success(
            user.copy(id = insertedId.toInt())
        )
    }

    suspend fun login(
        usernameInput: String,
        password: String
    ): AuthResult {

        val username = usernameInput.trim()

        if (username.isBlank() || password.isBlank()) {
            return AuthResult.Failure(
                "Please enter username and password."
            )
        }

        if (!PasswordHasher.isSupported()) {
            return AuthResult.Failure(
                "Authentication requires Android 8.0 or newer."
            )
        }

        val usernameKey = username.lowercase(Locale.ROOT)

        val user = userDao.findByUsername(usernameKey)
            ?: return AuthResult.Failure(
                "Username or password is incorrect."
            )

        val passwordMatches = withContext(Dispatchers.Default) {

            PasswordHasher.verify(
                password = password,
                encodedSalt = user.passwordSalt,
                encodedHash = user.passwordHash,
                algorithm = user.passwordAlgorithm,
                iterations = user.passwordIterations
            )
        }

        return if (passwordMatches) {

            AuthResult.Success(user)

        } else {

            AuthResult.Failure(
                "Username or password is incorrect."
            )
        }
    }

    private fun validatePassword(
        password: String
    ): String? {

        if (password.length < 8) {
            return "Password must contain at least 8 characters."
        }

        if (password.none { it.isLowerCase() }) {
            return "Password must contain a lowercase letter."
        }

        if (password.none { it.isUpperCase() }) {
            return "Password must contain an uppercase letter."
        }

        if (password.none { it.isDigit() }) {
            return "Password must contain a number."
        }

        if (
            password.none {
                !it.isLetterOrDigit() && !it.isWhitespace()
            }
        ) {
            return "Password must contain a symbol."
        }

        return null
    }
}