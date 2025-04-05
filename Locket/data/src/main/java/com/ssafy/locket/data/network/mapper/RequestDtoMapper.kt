package com.ssafy.locket.data.network.mapper

interface RequestDtoMapper<I, O> {
    fun map(input: I): O
}