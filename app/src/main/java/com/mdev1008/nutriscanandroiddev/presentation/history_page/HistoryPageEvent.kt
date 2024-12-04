package com.mdev1008.nutriscanandroiddev.presentation.history_page

sealed class HistoryPageEvent {
    data object GetSearchHistory: HistoryPageEvent()
}