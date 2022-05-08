package me.jason5lee.ktpost.adminRegister

import me.jason5lee.ktpost.common.AdminId
import me.jason5lee.ktpost.common.Password

interface AdminRegister : suspend (Password) -> AdminId
