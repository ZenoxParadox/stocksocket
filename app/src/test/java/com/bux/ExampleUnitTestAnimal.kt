package com.bux

import com.google.gson.Gson
import com.google.gson.GsonBuilder
import org.junit.Assert
import org.junit.Test


/**
 * Example local unit test, which will execute on the development machine (host).
 *
 * See [testing documentation](http://d.android.com/tools/testing).
 */
class ExampleUnitTestAnimal {

    open class Animal<T: Any> {
        lateinit var type: String
        lateinit var body: T
    }

    data class Cat(
        val hates: String
    ) : Animal<Cat>()

    data class Dog(
        val likes: String
    ) : Animal<Dog>()

    private val gson: Gson = GsonBuilder().create()

    private val CAT = "{\"type\": \"Cat\",\"hates\": \"Everything\"}"

    private val DOG = "{\"type\": \"Dog\",\"likes\": \"Treats\"}"

    @Test
    fun a1_cat() {
        val animal = gson.fromJson<Animal<*>>(CAT, Animal::class.java)
        Assert.assertEquals("Cat", animal.type)
    }

    @Test
    fun a2_dog() {
        val animal = gson.fromJson<Animal<*>>(DOG, Animal::class.java)
        Assert.assertEquals("Dog", animal.type)
    }

    /* ---------- */

    @Test
    fun b1_animal() {
        val cat = gson.fromJson<Animal<*>>(CAT, Animal::class.java)
        val dog = gson.fromJson<Animal<*>>(DOG, Animal::class.java)

        Assert.assertEquals("Cat", cat.type)
        Assert.assertEquals("Dog", dog.type)
    }

    @Test
    fun b2_cat() {
        val animal = gson.fromJson<Cat>(CAT, Cat::class.java)
        Assert.assertEquals("Cat", animal.type)
        Assert.assertEquals("Everything", animal.hates)
    }

    @Test
    fun b3_dog() {
        val animal = gson.fromJson<Dog>(DOG, Dog::class.java)
        Assert.assertEquals("Dog", animal.type)
        Assert.assertEquals("Treats", animal.likes)
    }

}