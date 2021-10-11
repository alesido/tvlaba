package org.alsi.android.domain.implementation.model

sealed class ImageReference(reference: String)

data class LocalVectorImageReference (val reference: String): ImageReference(reference)
data class LocalRasterImageReference (val reference: String): ImageReference(reference)
data class RemoteRasterImageReference (val reference: String): ImageReference(reference)