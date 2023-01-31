package cn.tabidachi.ext

fun String.isEmail() = matches("^\\w+([-+.]\\w+)*@\\w+([-.]\\w+)*\\.\\w+([-.]\\w+)*$".toRegex())

fun String.isValidUsername() = matches("[a-zA-Z0-9_\u30a1-\u30f6\u3041-\u3093\uff00-\uffff\u4e00-\u9fa5]+".toRegex())
