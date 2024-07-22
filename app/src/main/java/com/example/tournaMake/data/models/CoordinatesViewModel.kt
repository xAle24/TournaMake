package com.example.tournaMake.data.models

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.tournaMake.utils.Coordinates

class CoordinatesViewModel: ViewModel() {
    private val _coordinates = MutableLiveData<Coordinates>()
    val coordinatesLiveData: LiveData<Coordinates> = _coordinates

    fun changeCoordinates(coordinates: Coordinates) {
        _coordinates.postValue(coordinates)
    }
}