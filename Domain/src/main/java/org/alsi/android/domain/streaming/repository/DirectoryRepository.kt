package org.alsi.android.domain.streaming.repository

import io.reactivex.Completable

/** Each directory repository is attached to a certain streaming service.
 *
 * Knowing the service ID was primarily acquired to identify separate local
 * storage for service directory.
 */
abstract class DirectoryRepository(val streamingServiceId: Long) {

    abstract fun onLanguageChange(): Completable
}