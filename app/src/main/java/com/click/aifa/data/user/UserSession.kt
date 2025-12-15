package com.click.aifa.data.user

object UserSession {

    private var _user: UserWithFamily? = null

    val currentUser: UserWithFamily?
        get() = _user

    fun login(user: UserWithFamily) {
        _user = user
    }

    fun logout() {
        _user = null
    }
}
