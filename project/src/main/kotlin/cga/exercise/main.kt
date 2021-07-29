package cga.exercise

import cga.exercise.game.Game

fun main() { //aktuell kaputt
    val game = Game(1280, 720)
    println("${game.title} is starting..")
    println("You may close the game with Alt+F4!")
    game.run()
}