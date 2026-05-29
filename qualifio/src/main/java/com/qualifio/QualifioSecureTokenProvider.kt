package com.qualifio

import android.net.Uri

public fun interface QualifioSecureTokenProvider {
    public fun generateSecureToken(uri: Uri, callback: QualifioCallback<String?>)
}
