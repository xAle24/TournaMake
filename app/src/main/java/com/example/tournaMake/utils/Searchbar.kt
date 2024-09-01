package com.example.tournaMake.utils

class Searchbar <T>(private val allEntries: List<T>){
    private var filteredEntries : List<T> = allEntries

    fun filterEntries(predicate: (T) -> Boolean) {
        this.filteredEntries = this.allEntries.filter{predicate(it)}
    }
    fun getFilteredEntries() : List<T> {
        return filteredEntries
    }
}