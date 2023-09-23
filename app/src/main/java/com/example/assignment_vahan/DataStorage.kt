package com.example.assignment_vahan

class DataStorage {
    companion object {
        private val dataList = ArrayList<Items>()

        fun getDataList(): ArrayList<Items> {
            return dataList
        }

        fun updateDataList(newData: List<Items>) {
            dataList.clear()
            dataList.addAll(newData)
        }

    }
}