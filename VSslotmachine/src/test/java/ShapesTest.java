import static org.junit.Assert.assertNotNull;

import org.junit.Test;

/**
 * Pruebas de unidad de las figuras del paquete shapes (Circle, Rectangle
 * y Triangle) usadas en modo invisible. Mientras una figura no se haga
 * visible no debe dibujar nada ni abrir el lienzo (Canvas), así que estas
 * pruebas también corren sin pantalla: si alguna figura intentara dibujar,
 * la prueba fallaría con un error de entorno gráfico.
 */
public class ShapesTest
{
    /**
     * Un círculo invisible debería poder moverse en todas las direcciones
     * (con distancias positivas y negativas), cambiar de tamaño y de color
     * sin dibujarse.
     */
    @Test
    public void shouldMoveAndResizeInvisibleCircleWithoutDrawing()
    {
        Circle circle = new Circle();

        circle.moveRight();
        circle.moveLeft();
        circle.moveUp();
        circle.moveDown();
        circle.moveHorizontal(15);
        circle.moveHorizontal(-15);
        circle.moveVertical(10);
        circle.moveVertical(-10);
        circle.slowMoveHorizontal(5);
        circle.slowMoveHorizontal(-5);
        circle.slowMoveVertical(5);
        circle.slowMoveVertical(-5);
        circle.changeSize(20);
        circle.changeColor("red");
        circle.makeInvisible();

        assertNotNull(circle);
    }

    /**
     * Un rectángulo invisible debería poder moverse en todas las
     * direcciones, cambiar de tamaño y de color sin dibujarse.
     */
    @Test
    public void shouldMoveAndResizeInvisibleRectangleWithoutDrawing()
    {
        Rectangle rectangle = new Rectangle();

        rectangle.moveRight();
        rectangle.moveLeft();
        rectangle.moveUp();
        rectangle.moveDown();
        rectangle.moveHorizontal(15);
        rectangle.moveHorizontal(-15);
        rectangle.moveVertical(10);
        rectangle.moveVertical(-10);
        rectangle.slowMoveHorizontal(5);
        rectangle.slowMoveHorizontal(-5);
        rectangle.slowMoveVertical(5);
        rectangle.slowMoveVertical(-5);
        rectangle.changeSize(20, 30);
        rectangle.changeColor("blue");
        rectangle.makeInvisible();

        assertNotNull(rectangle);
    }

    /**
     * Un triángulo invisible debería poder moverse en todas las
     * direcciones, cambiar de tamaño y de color sin dibujarse.
     */
    @Test
    public void shouldMoveAndResizeInvisibleTriangleWithoutDrawing()
    {
        Triangle triangle = new Triangle();

        triangle.moveRight();
        triangle.moveLeft();
        triangle.moveUp();
        triangle.moveDown();
        triangle.moveHorizontal(15);
        triangle.moveHorizontal(-15);
        triangle.moveVertical(10);
        triangle.moveVertical(-10);
        triangle.slowMoveHorizontal(5);
        triangle.slowMoveHorizontal(-5);
        triangle.slowMoveVertical(5);
        triangle.slowMoveVertical(-5);
        triangle.changeSize(20, 30);
        triangle.changeColor("green");
        triangle.goUpRight(3);
        triangle.makeInvisible();

        assertNotNull(triangle);
    }
}
