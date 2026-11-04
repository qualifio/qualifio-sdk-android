package com.qualifio

public fun interface QualifioSecureTokenProvider {
    public fun generateSecureToken(campaign: String, callback: QualifioCallback<String?>)
}
