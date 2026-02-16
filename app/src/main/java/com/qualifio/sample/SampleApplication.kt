package com.qualifio.sample

import android.app.Application
import android.net.Uri
import android.util.Log
import com.auth0.jwt.JWT
import com.auth0.jwt.algorithms.Algorithm
import com.auth0.jwt.exceptions.JWTCreationException
import com.qualifio.Qualifio
import com.qualifio.QualifioConfiguration
import com.qualifio.QualifioListener
import com.qualifio.sample.storage.SampleDataStore
import dagger.hilt.android.HiltAndroidApp
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import java.util.Date
import javax.inject.Inject

@HiltAndroidApp
class SampleApplication : Application(), QualifioListener {
    @Inject
    lateinit var dataStore: SampleDataStore

    private val mainScope = MainScope()

    override fun onCreate() {
        super.onCreate()

        // Customise Qualifio.
        Qualifio.listener = this

        observeConfigurationState()

        mainScope.launch {
            val domain = dataStore.domainStream.first() ?: return@launch

            Qualifio.configure(
                context = this@SampleApplication,
                configuration = QualifioConfiguration(domain),
            )
        }
    }

    override suspend fun generateSecureToken(url: Uri): String? {
        val useSecureToken = dataStore.useSecureTokenStream.first()

        return if (useSecureToken) {
            try {
                val secureTokenFields = dataStore.secureTokenFieldsStream.first()
                val secureTokenSecret = dataStore.secureTokenSecretStream.first()
                val secureTokenSubject = dataStore.secureTokenSubjectStream.first()

                val algorithm = Algorithm.HMAC256(secureTokenSecret)

                val token = JWT.create()
                    .withSubject(secureTokenSubject)
                    .withIssuer(this.getString(R.string.jwt_issuer))
                    .withIssuedAt(Date())
                    .apply {
                        secureTokenFields?.let {
                            withClaim(
                                "form",
                                it,
                            )
                        }
                    }
                    .sign(algorithm)

                Log.e("token", token)

                token
            } catch (_: JWTCreationException) {
                return null
            }
        } else {
            return null
        }
    }

    fun observeConfigurationState() {
        mainScope.launch {
            dataStore.isConfigurationFinishedStream.distinctUntilChanged().collect { finished ->
                val showCampaignAtLaunch = dataStore.showCampaignAtLaunchStream.first()
                if (finished && showCampaignAtLaunch) {
                    delay(2_000)
                    Qualifio.launchCampaign()
                }
            }
        }
    }
}
