package ru.skillbranch.skillarticles.extensions

fun List<Pair<Int, Int>>.groupByBounds(bounds: List<Pair<Int, Int>>): List<MutableList<Pair<Int, Int>>> {
    return bounds.fold(mutableListOf<MutableList<Pair<Int, Int>>>()){acc, pair ->
        val res = this.filter {
            it.first >= pair.first && it.second <= pair.second
        }.toMutableList()
        acc.add(res)
        acc
    }
}