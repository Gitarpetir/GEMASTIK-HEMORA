package com.gemastik.hemora.data.auth.repository

import com.gemastik.hemora.data.school.dto.SchoolDto
import com.gemastik.hemora.data.auth.dto.UserDto
import com.gemastik.hemora.domain.auth.repository.AuthRepository
import com.gemastik.hemora.domain.model.User
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.tasks.await

class AuthRepositoryImpl(
    private val auth: FirebaseAuth,
    private val firestore: FirebaseFirestore
) : AuthRepository {

    override fun login(email: String, password: String): Flow<Result<User>> = flow {
        try {
            val authResult = auth.signInWithEmailAndPassword(email, password).await()
            val user = authResult.user
            if (user != null) {
                val document = firestore.collection("users").document(user.uid).get().await()
                if (document.exists()) {
                    val userDto = document.toObject(UserDto::class.java)
                    if (userDto != null) {
                        emit(Result.success(userDto.toDomain(user.uid)))
                    } else {
                        emit(Result.failure(Exception("Data pengguna tidak valid.")))
                    }
                } else {
                    emit(Result.failure(Exception("Pengguna tidak ditemukan di database.")))
                }
            } else {
                emit(Result.failure(Exception("Gagal login. Email atau password salah.")))
            }
        } catch (e: Exception) {
            emit(Result.failure(e))
        }
    }

    override fun registerRemajaPutri(
        name: String,
        email: String,
        password: String,
        schoolCode: String
    ): Flow<Result<User>> = flow {
        try {
            // 1. Verify schoolCode
            val schoolsSnapshot = firestore.collection("schools")
                .whereEqualTo("schoolCode", schoolCode)
                .get()
                .await()

            if (schoolsSnapshot.isEmpty) {
                emit(Result.failure(Exception("Kode sekolah tidak valid.")))
                return@flow
            }
            val schoolId = schoolsSnapshot.documents.first().id

            // 2. Create Firebase Auth user
            val authResult = auth.createUserWithEmailAndPassword(email, password).await()
            val firebaseUser = authResult.user

            if (firebaseUser != null) {
                // 3. Save to Firestore
                val userDto = UserDto(
                    name = name,
                    email = email,
                    role = "REMAJA_PUTRI",
                    schoolId = schoolId
                )
                firestore.collection("users").document(firebaseUser.uid).set(userDto).await()
                
                emit(Result.success(userDto.toDomain(firebaseUser.uid)))
            } else {
                emit(Result.failure(Exception("Gagal membuat akun.")))
            }
        } catch (e: Exception) {
            emit(Result.failure(e))
        }
    }

    override fun registerUks(
        name: String,
        email: String,
        password: String,
        schoolName: String,
        activationCode: String
    ): Flow<Result<User>> = flow {
        try {
            // 1. Validate Activation Code
            val activationDoc = firestore.collection("activation_codes").document("UKS").get().await()
            if (!activationDoc.exists()) {
                emit(Result.failure(Exception("Sistem belum siap untuk pendaftaran UKS.")))
                return@flow
            }
            val validCode = activationDoc.getString("code")
            if (validCode != activationCode) {
                emit(Result.failure(Exception("Kode Aktivasi UKS tidak valid.")))
                return@flow
            }

            // 2. Create Firebase Auth User
            val authResult = auth.createUserWithEmailAndPassword(email, password).await()
            val firebaseUser = authResult.user
            
            if (firebaseUser != null) {
                // 3. Generate Random School Code
                val chars = "ABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789"
                val generatedSchoolCode = (1..6).map { chars.random() }.joinToString("")
                
                // 4. Create School Document
                val schoolDto = SchoolDto(schoolName = schoolName, schoolCode = generatedSchoolCode)
                val schoolRef = firestore.collection("schools").document()
                schoolRef.set(schoolDto).await()
                
                // 5. Create User Document
                val userDto = UserDto(
                    name = name,
                    email = email,
                    role = "UKS",
                    schoolId = schoolRef.id
                )
                firestore.collection("users").document(firebaseUser.uid).set(userDto).await()
                
                emit(Result.success(userDto.toDomain(firebaseUser.uid)))
            } else {
                emit(Result.failure(Exception("Gagal membuat akun.")))
            }
        } catch (e: Exception) {
            emit(Result.failure(e))
        }
    }

    override fun getCurrentUser(): Flow<Result<User?>> = callbackFlow {
        val currentUser = auth.currentUser
        if (currentUser == null) {
            trySend(Result.success(null))
            close()
            return@callbackFlow
        }
        
        val listener = firestore.collection("users").document(currentUser.uid)
            .addSnapshotListener { snapshot, error ->
                if (error != null) {
                    trySend(Result.failure(error))
                    return@addSnapshotListener
                }
                
                if (snapshot != null && snapshot.exists()) {
                    val userDto = snapshot.toObject(UserDto::class.java)
                    if (userDto != null) {
                        trySend(Result.success(userDto.toDomain(currentUser.uid)))
                    }
                } else {
                    trySend(Result.success(null))
                }
            }
            
        awaitClose { listener.remove() }
    }

    override suspend fun logout() {
        auth.signOut()
    }
}
