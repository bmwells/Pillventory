package com.example.apptest.data


import android.content.ContentValues.TAG
import android.util.Log
import com.google.firebase.auth.AuthResult
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.tasks.await
import javax.inject.Inject

// Main use of Firebase Authentication - related functions
class AuthRepositoryImpl @Inject constructor(
    private val firebaseAuth: FirebaseAuth
): AuthRepository {

    // Login the user with provided email and password
    override fun loginUser(email: String, password: String): Flow<Resource<AuthResult>> {
        return flow {
            emit(Resource.Loading())
            val result = firebaseAuth.signInWithEmailAndPassword(email, password).await() // wait until firebase sign in with email and password op is done and get result
            emit(Resource.Success(result))
        }.catch {
            emit(Resource.Error(it.message.toString())) // represent system error messages
        }
    }

    // Registers user with provided email and password
    override fun registerUser(email: String, password: String): Flow<Resource<AuthResult>> {
        return flow {
            emit(Resource.Loading())
            val result = firebaseAuth.createUserWithEmailAndPassword(email, password).await() // wait until firebase register with email and password op is done and get result
            emit(Resource.Success(result))
        }.catch {
            emit(Resource.Error(it.message.toString())) // represent system error messages
        }
    }

    // Allows user to login as a guest by keeping them anonymous
    override fun anonymousUser(): Flow<Resource<AuthResult>> {
        return flow {
            emit(Resource.Loading())
            val result = firebaseAuth.signInAnonymously().await()
            emit(Resource.Success(result))
        }.catch {
            emit(Resource.Error(it.message.toString())) // represent system error messages
        }
    }

    // delete the current user from the Firebase Auth database
    override fun deleteUser() {
        val currentUser = firebaseAuth.currentUser!!

        currentUser.delete()
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    Log.d(TAG, "User account deleted.")
                }
                /**
                 * Message show that account deleted will show in logcat
                 * FirebaseUser method delete() returns void -> can't emit a result
                 */
            }
    }

    // Logout the current user
    override fun logoutUser(): Flow<Resource<Boolean>> {
        return flow {
            emit(Resource.Loading())
            firebaseAuth.signOut()
            emit(Resource.Success(true))
        }.catch {
            emit(Resource.Error(it.message.toString())) // Handle error if logout fails
        }
    }

    // get the user id (UID) to be used for Realtime database
    override fun getUID(): String? {
        return firebaseAuth.currentUser?.uid
    }

    // Function to fetch the current users email. To be used in the Profile section.
    override fun getUserEmail(): String? {
        return firebaseAuth.currentUser!!.email
    }

    // Check if user is anonymous - will determine if they are a guest user
    override fun isUserAnonymous(): Boolean {
        return firebaseAuth.currentUser!!.isAnonymous

    }

    // sends user email to reset their account password
    override fun sendVerification(emailAddress: String) {

        firebaseAuth.sendPasswordResetEmail(emailAddress)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    Log.d(TAG, "Email sent.")
                }
            }
    }

    // sends user verification email
    override fun sendUserEmailVerification() {
        val user = firebaseAuth.currentUser

        user?.let {
            it.sendEmailVerification()
                .addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                        Log.d(TAG, "Email sent.")
                    }
                }
        }
    }

    // function to check if the user has verified their email
    override fun isCurrentUserVerified(): Boolean {
        val currentUser = firebaseAuth.currentUser
        return currentUser?.isEmailVerified == true
    }

    private var itemRecordId: String? = null
    override fun setItemRecordId(itemId: String) {
        this.itemRecordId = itemId
    }

    override fun getItemRecordId(): String? {
        return itemRecordId
    }

}
