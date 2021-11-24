package org.alsi.android.local.mapper

import org.alsi.android.data.framework.mapper.EntityMapper
import org.alsi.android.domain.user.model.UserPreferences
import org.alsi.android.local.model.user.UserPreferencesEntity

class UserPreferencesMapper: EntityMapper<UserPreferencesEntity, UserPreferences> {

    override fun mapFromEntity(entity: UserPreferencesEntity): UserPreferences =
        with(entity) {
            UserPreferences(loginRememberMe, fontSize)
        }


    override fun mapToEntity(domain: UserPreferences): UserPreferencesEntity =
        with(domain) {
            UserPreferencesEntity(0L, loginRememberMe, fontSize)
        }
}