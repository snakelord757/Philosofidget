package ru.snakelord.philosofidget.quotetopics.data

interface TopicSelectionStore {
    fun selectedId(): String?
    fun saveSelectedId(id: String)
}
