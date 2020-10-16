package ru.emkn.kotlin

import com.google.gson.annotations.SerializedName

class Word(index: Long) {
    @SerializedName("category") var category = ""
    @SerializedName("id") val id = index
    @SerializedName("forms") var usedForms = mutableListOf<String>()
    @SerializedName("pageIndex") var pageIndex = mutableListOf<Int>()
    @SerializedName("lineIndex") var lineIndex = mutableListOf<Int>()
    @SerializedName("amount") var amount = 0

    fun increaseAmount() { this.amount++ }

    fun addForm(form: String) {
        if (!usedForms.contains(form))
            usedForms.add(form)
    }

    fun addPage(page: Int) {
        if (!pageIndex.contains(page))
            pageIndex.add(page)
    }

    fun addLine(line: Int) {
        if (!lineIndex.contains(line))
            lineIndex.add(line)
    }
}
