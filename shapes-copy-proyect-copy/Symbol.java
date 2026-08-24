
/**
 * Write a description of class simbol here.
 * 
 * @author (your name) 
 * @version (a version number or a date)
 */
/**
 * Representa un símbolo de la máquina tragamonedas.
 */
/**
 * Representa un símbolo de la máquina tragamonedas.
 */
public class Symbol
{
    private String color;
    private Circle circle;

    public Symbol(String color)
    {
        this.color = color;

        circle = new Circle();
        circle.changeColor(color);
    }

    public String getColor()
    {
        return color;
    }

    public void makeVisible()
    {
        circle.makeVisible();
    }

    public void makeInvisible()
    {
        circle.makeInvisible();
    }

    public void moveHorizontal(int distance)
    {
        circle.moveHorizontal(distance);
    }

    public void moveVertical(int distance)
    {
        circle.moveVertical(distance);
    }

    public void setPosition(int x, int y)
    {
        circle.moveHorizontal(x - 20);
        circle.moveVertical(y - 15);
    }
}




