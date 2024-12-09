package processing.core

import processing.core.PApplet

class ProcessingTest : PApplet() {
    override fun settings() {
        size(400, 400)
    }

    override fun setup() {
        background(0)
    }

    override fun draw() {
        fill(255)
        ellipse(mouseX.toFloat(), mouseY.toFloat(), 20f, 20f)
    }
}
fun main() {
    PApplet.main(ProcessingTest::class.java)
}

fun settings(action: ProcessingTest.() -> Unit){

}
fun setup(action: ProcessingTest.() -> Unit){

}
fun draw(action: ProcessingTest.() -> Unit){

}