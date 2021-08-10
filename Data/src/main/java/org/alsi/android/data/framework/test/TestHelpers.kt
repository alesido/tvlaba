package org.alsi.android.data.framework.test

import java.io.File

fun readJsonResourceFile(path: String) : String
{
    val uri = object{}.javaClass.classLoader.getResource(path)
    uri?: return "{}"
    val file = File(uri.path)
    return String(file.readBytes())
}

