package moe.tabidachi.ext

fun String.isEmail() = matches("^\\w+([-+.]\\w+)*@\\w+([-.]\\w+)*\\.\\w+([-.]\\w+)*$".toRegex())

fun String.isValidUsername() = matches("[a-zA-Z0-9_\u30a1-\u30f6\u3041-\u3093\uff00-\uffff\u4e00-\u9fa5]+".toRegex())

fun String.isUidQuery() = matches(Regex("(?i)(^\\s*)uid\\s*([:：])?\\s*[0-9]+\\s*$"))

fun String.regex(): Regex {
    return this.toCharArray().joinToString("", "^", ".+") {
        "(?=.*$it)"
    }.toRegex()
}