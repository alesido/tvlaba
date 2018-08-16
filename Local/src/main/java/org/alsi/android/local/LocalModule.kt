package org.alsi.android.local

import android.content.Context
import dagger.Module
import dagger.Provides
import io.objectbox.BoxStore
import io.reactivex.subjects.PublishSubject
import org.alsi.android.MyObjectBox
import org.alsi.android.local.model.user.UserAccountEntity
import javax.inject.Named
import javax.inject.Singleton

@Module
class LocalModule {

    @Singleton
    @Provides
    @Named(Local.STORE_NAME)
    fun provideCommonLocalStore(context: Context): BoxStore {
        return MyObjectBox.builder().name(Local.STORE_NAME).androidContext(context).build()
    }

    @Singleton @Provides fun provideUserAccountSubject(): PublishSubject<UserAccountEntity> = PublishSubject.create()
}