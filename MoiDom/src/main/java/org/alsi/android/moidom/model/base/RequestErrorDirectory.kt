package org.alsi.android.moidom.model.base

import org.alsi.android.local.model.settings.RcFunctionProperty

/**
 *  Complete list of the API error codes with supposed server messages.
 */
enum class RequestErrorDirectory(val code: Long, val en: String, val ru: String) {

    UNKNOWN_ERROR(0, "Unknown error", "Неизвестная ошибка"),
    INCORRECT_REQUEST(1, "Incorrect request", "Неверный запрос"),
    WRONG_LOGIN(2, "Wrong login or password", "Неправильный логин или пароль"),
    ACCESS_DENIED(3, "Access denied", "Доступ запрещен"),
    LOGIN_INCORRECT(4, "Login incorrect", "Неправильный логин"),
    CONTRACT_INACTIVE(5, "Your contract is inactive", "Ваш контракт неактивен"),
    CONTRACT_PAUSED(6, "Your contract is paused", "Ваш контракт заморожен"),
    CHANNEL_NOT_FOUND(7, "Channel not found or not allowed", "Канал не найден или недоступен"),
    ERROR_GENERATING_URL(8, "Error generate URL. Bad parameters", "Ошибка генерации URL – заданы неверные параметры"),
    NEED_PARAMETER_DAY(9, "Need DAY parameter <DDMMYY>", "Нужен параметр DAY"),
    NEED_PARAMETER_CHANNEL_ID(10, "Need Channel ID", "Нужен ID канала"),
    ANOTHER_LOGGED(11, "Another client with your login was logged", "Другой клиент вошел под Вашим логином"),
    AUTHENTICATION_ERROR(12, "Authentication error", "Ошибка аутентификации"),
    PACKET_EXPIRED(13, "Your packet was expired", "Ваш пакет просрочен"),
    UNKNOWN_API_CALL(14, "Unknown API function", "Неизвестная функция API"),
    ARCHIVE_NOT_AVAILABLE(15, "Archive is not available", "Архив недоступен"),
    NEED_PLACE_TO_SET(16, "Need place to set", "Необходимо установить в набор"),
    NEED_VARIABLE_NAME(17, "Need name of settings variable", "Нужно название переменной установки"),
    INCORRECT_CONFIRMATION_CODE(18, "Incorrect confirmation code", "Неверный код подтверждения"),
    WRONG_PASSWORD_CURRENT(19, "Current password is wrong", "Текущий пароль неверен"),
    WRONG_PASSWORD_NEW(20, "New password is wrong", "Новый пароль неверен"),
    NEED_PARAMETER_VALUE(21, "Need value (val) parameter", "Необходим параметр val"),
    VALUE_NOT_ALLOWED(22, "This value is not allowed", "Это значение недопустимо"),
    NEED_PARAMETER(23, "Need parameter", "Нужен параметр"),
    NEED_PARAMETER_ID(24, "Need parameter <id>", "Нужен параметр <id>"),
    NEED_PARAMETER_FILE_ID(25, "Need parameter <fileid>", "Нужен параметр < fileid >"),
    NEED_PARAMETER_TYPE(26, "Need parameter <type>", "Нужен параметр <type>"),
    NEED_PARAMETER_QUERY(27, "Need parameter <query>", "Нужен параметр <query>"),
    NEED_PARAMETER_BITRATE(29, "Need parameter <bitrate>", "Нужен параметр < bitrate") ,
    SERVICE_NOT_AVAILABLE(30, "Service is not available", "Сервис недоступен"),
    QUERY_LIMIT_EXCEEDED(31, "Query limit exceeded", "Исчерпан лимит запросов"),
    RULE_ALREADY_EXIST(32, "Rule already exist", "Правило уже существует"),
    NEED_PARAMETER_CHANNEL_COMMAND(33, "Need param ?cmd = hide_channel | show_channel | get_list", "Нужен параметр ?cmd = hide_channel |,show_channel | get_list"),
    NEED_PARAMETER_USER_COMMAND(34, "Need param ?cmd = get_user_rates | set_user_rates", "Нужен параметр ?cmd = get_user_rates |,set_user_rates"),
    BAD_RATE_VALUE(35, "Bad rate value. Allow <show|hide|pass>", "Неверное значение рейтинга"),
    FILM_NOT_FOUND(36, "Can’t find film", "Невозможно найти фильм"),
    FILM_ALREADY_FAVORITE(37, "This film already added to favorite list", "Этот фильм уже добавлен в список избранных"),
    SYSTEM_ERROR(99, "System error", "Ошибка системы");

    companion object {
        val valueByCode = values().map { it.code to it}.toMap()
    }
}