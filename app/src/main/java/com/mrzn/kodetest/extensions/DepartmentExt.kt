package com.mrzn.kodetest.extensions

import com.mrzn.kodetest.R
import com.mrzn.kodetest.domain.entity.Department

val Department?.labelResId: Int
    get() = when (this) {
        Department.ANDROID -> R.string.department_android
        Department.IOS -> R.string.department_ios
        Department.DESIGN -> R.string.department_design
        Department.MANAGEMENT -> R.string.department_management
        Department.QA -> R.string.department_qa
        Department.BACK_OFFICE -> R.string.department_back_office
        Department.FRONTEND -> R.string.department_frontend
        Department.HR -> R.string.department_hr
        Department.PR -> R.string.department_pr
        Department.BACKEND -> R.string.department_backend
        Department.SUPPORT -> R.string.department_support
        Department.ANALYTICS -> R.string.department_analytics
        null -> R.string.label_all
    }