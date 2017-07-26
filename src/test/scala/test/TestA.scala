package test

/**
  * Created by zhangzhiqiang on 2017/7/11.
  */
object TestA {

  //Person1 is to show: primary constructor consists of not only class args list but also all runnable part in class body.
  class Person1(var firstName: String, var lastName: String) {
    println("the constructor begins")
    // some class fields
    private val HOME = "/root"
    var age = 30
    // some methods
    override def toString = s"$firstName $lastName is $age years old"

    def printHome { println(s"HOME = $HOME") }
    def printFullName { println(this) } // uses toString

    println("still in the constructor")
  }



  def main(args: Array[String]) {
    val p1 = new Person1("Tome","White")
    println("---------------------------------")

  }
}
