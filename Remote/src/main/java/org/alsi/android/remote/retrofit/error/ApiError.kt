package org.alsi.android.remote.retrofit.error

/** Quite general error/return codes/cases enumeration collected from
 * streaming services APIs implemented.
 *
 * This enumeration provided to:
 *
 * 1) Use in business logic, e.g. to suggest subscriptions, or deny access
 * to content under parent control, etc.
 *
 * 2) Provide unified messages and their translation.
 */
enum class ApiError {

    UNCLASSIFIED_ERROR,

    INCORRECT_LOGIN,
    WRONG_LOGIN_NAME_OR_PASSWORD,
    WRONG_CONFIRMATION_CODE,
    AUTHENTICATION_ERROR,
    ALREADY_LOGGED_IN_ON_ANOTHER_DEVICE,

    SESSION_EXPIRED,
    SESSION_ID_ILLEGAL,
    SYSTEM_ERROR,
    SERVICE_NOT_AVAILABLE,
    QUERY_LIMIT_EXCEEDED,

    ENDPOINT_NOT_FOUND,
    INCORRECT_REQUEST,
    URL_PARAMETER_REQUIRED,
    BAD_PARAMETER_VALUE,
    WRONG_URL_PARAMETERS,
    UNKNOWN_API_ENDPOINT,

    CONTRACT_EXPIRED,
    CONTRACT_SUSPENDED,
    PACKET_EXPIRED,
    SUBSCRIPTION_EXPIRED,
    SUBSCRIPTION_INACTIVE,
    ACCESS_DENIED,

    UNDER_PARENT_CONTROL,
    WRONG_PARENT_CONTROL_PASSWORD,

    DATA_ITEM_NOT_FOUND, // channel/program/video/etc. not found
    DATA_ITEM_ALREADY_ADDED,
    DATA_ITEM_ALREADY_EXISTS,
}