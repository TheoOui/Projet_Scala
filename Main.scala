import scala.collection.immutable.ArraySeq
import scala.io.Source

/**
 * Main app containg program loop
 */
object Main extends App {

  println("Starting application")

  val status = run()

  println("\nExiting application")
  println(s"Final status: ${status.message}")

  /**
   * Read action from Stdin and execute it
   * Exit if action is 'exit' or if an error occured (status > 0)
   * DO NOT MODIFY THIS FUNCTION
   */
  def run(canvas: Canvas = Canvas()): Status = {
    println("\n======\nCanvas:")
    canvas.display

    print("\nAction: ")

    val action = scala.io.StdIn.readLine()

    val (newCanvas, status) = execute(ArraySeq.unsafeWrapArray(action.split(' ')), canvas)

    if (status.error) {
      println(s"ERROR: ${status.message}")
    }

    if (status.exit) {
      status 
    } else {
      run(newCanvas)  
    }
  }

  /**
   * Execute various actions depending on an action command and optionnaly a Canvas
   */
  def execute(action: Seq[String], canvas: Canvas): (Canvas, Status) = {
    val execution: (Seq[String], Canvas) => (Canvas, Status) = action.head match {
      case "exit" => Canvas.exit
      case "dummy" => Canvas.dummy
      case "dummy2" => Canvas.dummy2
      case "new_canvas" => Canvas.createCanvas
      case "load_image" => Canvas.loadImage
      case "update_pixel" => Canvas.updatePixel
      case "drawline" => Canvas.drawLine
      case "drawRectangle" => Canvas.drawRectangle
      // TODO: Add command here
      case _ => Canvas.default
    }

    execution(action.tail, canvas)
  }
}

/**
 * Define the status of the previous execution
 */
case class Status(
  exit: Boolean = false,
  error: Boolean = false,
  message: String = ""
)

/**
 * A pixel is defined by its coordinates along with its color as a char
 */
case class Pixel(x: Int, y: Int, color: Char = ' ') {
  override def toString(): String = {
    color.toString
  }
}

/**
 * Companion object of Pixel case class
 */
object Pixel {
  /**
   * Create a Pixel from a string "x,y"
   */
  def apply(s: String): Pixel = {
    s.split(',') match {
      case Array(x, y) => Pixel(x.toInt, y.toInt, '.')
      case _ => Pixel(0, 0)
    }
  }

  /**
   * Create a Pixel from a string "x,y" and a color 
   */
  def apply(s: String, color: Char): Pixel = {
    s.split(',') match {
      case Array(x, y) => Pixel(x.toInt, y.toInt, color)
      case _ => Pixel(0, 0)
    }
  }
}

/**
 * A Canvas is defined by its width and height, and a matrix of Pixel
 */
case class Canvas(width: Int = 0, height: Int = 0, pixels: Vector[Vector[Pixel]] = Vector()) {

  /**
   * Print the canvas in the console
   */
  def display: Unit = {
    if (pixels.size == 0) {
      println("Empty Canvas")
    } else {
      println(s"Size: $width x $height")
      pixels.foreach(row => {
        row.foreach(pixel => print(pixel))
        println
      })
    }
  }

  /**
   * Takes a pixel in argument and put it in the canvas
   * in the right position with its color
   */
  def update(pixel: Pixel): Canvas = {
    
    val newPixels = pixels
      .updated(pixel.y, pixels(pixel.y).updated(pixel.x, pixel))

    this.copy(pixels = newPixels)
  }

  /**
   * Return a Canvas containing all modifications
   */
  def updates(pixels: Seq[Pixel]): Canvas = {
    pixels.foldLeft(this)((f, p) => f.update(p))
  }
  
  // TODO: Add any useful method
}

/**
 * Companion object for Canvas case class
 */
object Canvas {
  /**
   * Exit execution
   */
  def exit(arguments: Seq[String], canvas: Canvas): (Canvas, Status) = 
    (canvas, Status(exit = true, message="Received exit signal"))

  /**
   * Default execution for unknown action
   */
  def default(arguments: Seq[String], canvas: Canvas): (Canvas, Status) = 
    (canvas, Status(error = true, message = s"Unknown command"))

  /**
   * Create a static Canvas
   */
  def dummy(arguments: Seq[String], canvas: Canvas): (Canvas, Status) = 
    if (arguments.size > 0) 
      (canvas, Status(error = true, message = "dummy action does not excpect arguments"))
    else  {
      val dummyCanvas = Canvas(
        width = 3,
        height = 4,
        pixels = Vector(
          Vector(Pixel(0, 0, '#'), Pixel(1, 0, '.'), Pixel(2, 0, '#')),
          Vector(Pixel(0, 1, '#'), Pixel(1, 1, '.'), Pixel(2, 1, '#')),
          Vector(Pixel(0, 2, '#'), Pixel(1, 2, '.'), Pixel(2, 2, '#')),
          Vector(Pixel(0, 3, '#'), Pixel(1, 3, '.'), Pixel(2, 3, '#'))
        )
      )
      
      (dummyCanvas, Status())
    }

  /**
   * Create a static canvas using the Pixel companion object
   */
  def dummy2(arguments: Seq[String], canvas: Canvas): (Canvas, Status) = {
    if (arguments.size > 0) 
      (canvas, Status(error = true, message = "dummy action does not excpect arguments"))
    else  {
      val dummyCanvas = Canvas(
        width = 3,
        height = 1,
        pixels = Vector(
          Vector(Pixel("0,0", '#'), Pixel("1,0"), Pixel("2,0", '#')),
        )
      )
      
      (dummyCanvas, Status())
    }
  }

  def createCanvas(arguments: Seq[String], canvas: Canvas): (Canvas, Status) = {
    if (arguments.size != 3) 
      (canvas, Status(error = true, message = "createCanvas action excpect 3 arguments"))
    else  {
      val newCanvas = Canvas(
        width = arguments(0).toInt,
        height = arguments(1).toInt,
        pixels = Vector.fill(arguments(1).toInt, arguments(0).toInt)(Pixel(0, 0, arguments(2).charAt(0)))
      )
      (newCanvas, Status()) 
    }
  }

  def loadImage(arguments: Seq[String], canvas: Canvas): (Canvas, Status) = {
    if (arguments.size != 1) 
      (canvas, Status(error = true, message = "createCanvas action excpect 1 argument"))
    else  {
      val content = Source.fromFile(arguments(0)).getLines().toVector

      print(content.size)
      print(content(0).size)

      val newCanvas = Canvas(
        width = content.size,
        height = content(0).size,
        pixels = (0 until content.size).map(x => (0 until content(0).size).map(y => Pixel(x, y, content(x).charAt(y))).toVector).toVector
      )
      (newCanvas, Status()) 
    }
  }

  def updatePixel(arguments: Seq[String], canvas: Canvas): (Canvas, Status) = {
    val (c,s) = checkInput(arguments, 2, canvas)
    if (s.error) return (c,s)
    else{
      val newCanvas = canvas.update(Pixel(arguments(0).split(',')(0).toInt, arguments(0).split(',')(1).toInt, arguments(1).charAt(0)))
      (newCanvas, Status()) 
    }
  }

  def drawLine(arguments: Seq[String], canvas: Canvas): (Canvas, Status) = {
    val (c,s) = checkInput(arguments, 3, canvas)
    if (s.error) return (c,s)
    else{

      val x1 = arguments(0).split(',')(0).toInt
      val y1 = arguments(0).split(',')(1).toInt
      val x2 = arguments(1).split(',')(0).toInt
      val y2 = arguments(1).split(',')(1).toInt
      val color = arguments(2).charAt(0)

      if (x1 == x2) {
        val newCanvas = canvas.updates((y1 to y2).map(y => Pixel(x1, y, color)))
        (newCanvas, Status()) 
      } else if (y1 == y2) {
        val newCanvas = canvas.updates((x1 to x2).map(x => Pixel(x, y1, color)))
        (newCanvas, Status()) 
      } else {
        (canvas, Status(error = true, message = "drawLine action only supports horizontal and vertical lines"))
      }
    }  
  }

  def checkInput(arguments: Seq[String], argumentSize: Int ,canvas: Canvas): (Canvas, Status) = {
    if (arguments.size != argumentSize){
      (canvas, Status(error = true, message = "Action expects " + argumentSize + " arguments"))
    }
    else if ((arguments(0).split(',')(0).toInt > canvas.width)
        || (arguments(0).split(',')(1).toInt > canvas.height) 
          || (arguments(1).split(',')(0).toInt > canvas.width) 
            || (arguments(1).split(',')(1).toInt > canvas.height)){
      (canvas, Status(error = true, message = "one or more argument(s) are out of bounds"))
        }
    else {
      (canvas, Status()) 
    }
  }

  def drawRectangle(arguments: Seq[String], canvas: Canvas): (Canvas, Status) = {
    val (c,s) = checkInput(arguments, 3, canvas)
    if (s.error) return (c,s)
    else{
      val x1 = arguments(0).split(',')(0).toInt
      val y1 = arguments(0).split(',')(1).toInt
      val x2 = arguments(1).split(',')(0).toInt
      val y2 = arguments(1).split(',')(1).toInt
      val color = arguments(2).charAt(0)

      val newCanvas = canvas.updates((x1 to x2).map(x => Pixel(x, y1, color)))
      val newCanvas2 = newCanvas.updates((x1 to x2).map(x => Pixel(x, y2, color)))
      val newCanvas3 = newCanvas2.updates((y1 to y2).map(y => Pixel(x1, y, color)))
      val newCanvas4 = newCanvas3.updates((y1 to y2).map(y => Pixel(x2, y, color)))
      (newCanvas4, Status()) 
    }
  }
}
