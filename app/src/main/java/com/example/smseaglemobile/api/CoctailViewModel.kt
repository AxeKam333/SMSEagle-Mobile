package com.example.smseaglemobile.api

import android.util.Log
import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.smseaglemobile.api.CocktailApi
//import com.example.smseaglemobile.api.Coctail
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class CocktailViewModel : ViewModel() {
    private val _cocktails = MutableStateFlow<List<Cocktail>>(emptyList())
    val cocktails: StateFlow<List<Cocktail>> = _cocktails

    private val _selectedCocktail = MutableStateFlow<Cocktail?>(null)
    val selectedCocktail: StateFlow<Cocktail?> = _selectedCocktail

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading

    private val api = CocktailApi.instance

//    init {
//        loadCocktails()
//    }

    fun loadCocktails(query: String = "a") {
        viewModelScope.launch {
            _isLoading.value = true
            try {
                val response = api.searchCocktails(query)
                _cocktails.value = response.drinks?.map { it.toCocktail() } ?: emptyList()
            } catch (e: Exception) {
                // Obsługa błędów
                Log.e("CocktailViewModel", "Error loading cocktails", e)
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun getCocktailDetails(id: String) {
        viewModelScope.launch {
            _isLoading.value = true
            try {
                val response = api.getCocktailDetails(id)
                _selectedCocktail.value = response.drinks?.firstOrNull()?.toCocktail()
            } catch (e: Exception) {
                Log.e("CocktailViewModel", "Error loading cocktails", e)
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun loadCocktailsByAlcoholic(alcoholic: String) {
        viewModelScope.launch {
            _isLoading.value = true
            try {
                val response = api.getByAlcoholic(alcoholic)
                _cocktails.value = response.drinks?.map { it.toCocktail() } ?: emptyList()
            } catch (e: Exception) {
                Log.e("CocktailViewModel", "Error loading cocktails", e)
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun loadCocktailsByCategory(category: String) {
        viewModelScope.launch {
            _isLoading.value = true
            try {
                val response = api.getByCategory(category)
                _cocktails.value = response.drinks?.map { it.toCocktail() } ?: emptyList()
            } catch (e: Exception) {
                Log.e("CocktailViewModel", "Error loading cocktails", e)
            } finally {
                _isLoading.value = false
            }
        }
    }

    private val _searchQuery = mutableStateOf("")
    val searchQuery: State<String> = _searchQuery

    fun updateSearchQuery(query: String) {
        _searchQuery.value = query
        searchCoctails(query)
    }

    fun searchCoctails(query: String) {
        viewModelScope.launch {
            _isLoading.value = true
            try {
                val response = api.searchCocktails(query)
                _cocktails.value = response.drinks?.map { it.toCocktail() } ?: emptyList()
            } catch (e: Exception) {
                Log.e("CocktailViewModel", "Error loading cocktails", e)
            } finally {
                _isLoading.value = false
            }
        }
    }
}
