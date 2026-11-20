package com.qualifio.sample

import android.app.Application
import com.auth0.jwt.JWT
import com.auth0.jwt.algorithms.Algorithm
import com.qualifio.Qualifio
import com.qualifio.QualifioConfiguration
import com.qualifio.sample.storage.SampleDataStore
import dagger.hilt.android.HiltAndroidApp
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import timber.log.Timber
import java.util.Date
import javax.inject.Inject

private const val CAMPAIGN_ID_CLAIM = "campaignId"
private const val FORM_CLAIM = "form"

internal const val SAMPLE_CAMPAIGN = "sdk-xmas"

@HiltAndroidApp
class SampleApplication : Application() {
    @Inject
    lateinit var dataStore: SampleDataStore

    private val mainScope = MainScope()

    override fun onCreate() {
        super.onCreate()

        Timber.plant(Timber.DebugTree())

        // Customise Qualifio.
        Qualifio.setSecureTokenProvider(::generateSecureToken)

        mainScope.launch {
            val domain = dataStore.domainStream.first() ?: return@launch

            Qualifio.configure(
                context = this@SampleApplication,
                configuration = QualifioConfiguration(domain),
            )
        }
    }

    suspend fun generateSecureToken(campaign: String): String? {
        if (!dataStore.useSecureTokenStream.first()) {
            return null
        }

        val secret = dataStore.secureTokenSecretStream.first()
        val subject = dataStore.secureTokenSubjectStream.first()
        val fields = dataStore.secureTokenFieldsStream.first().orEmpty()

        return try {
            JWT.create()
                .withSubject(subject)
                .withIssuer(getString(R.string.jwt_issuer))
                .withIssuedAt(Date())
                .apply {
                    fields[CAMPAIGN_ID_CLAIM]?.let { withClaim(CAMPAIGN_ID_CLAIM, it) }

                    val formFields = fields.filterKeys { it != CAMPAIGN_ID_CLAIM }
                    if (formFields.isNotEmpty()) {
                        withClaim(FORM_CLAIM, formFields)
                    }
                }
                .sign(Algorithm.HMAC256(secret))
        } catch (e: Exception) {
            // Signing fails with an IllegalArgumentException (not a JWTCreationException) for a blank secret.
            Timber.e(e, "Failed to generate the secure token.")
            null
        }
    }
}
