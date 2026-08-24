
/**
 * Write a description of class Wheel here.
 * 
 * @author (your name) 
 * @version (a version number or a date)
 */
/**
 * Representa una rueda de la máquina tragamonedas.
 */
/**
 * Representa una rueda de la máquina tragamonedas.
 */
/**
 * Representa una rueda de la máquina tragamonedas.
 */
/**
 * Representa una rueda de la máquina tragamonedas.
 */
public class Wheel
{
    private Symbol[] symbols;
    private int currentPosition;
    private int xPosition;
    private int yPosition;

    public Wheel()
    {
        symbols = new Symbol[0];
        currentPosition = 0;
        xPosition = 50;
        yPosition = 50;
    }

    public Wheel(int x, int y)
    {
        symbols = new Symbol[0];
        currentPosition = 0;
        xPosition = x;
        yPosition = y;
    }

    public void addSymbol(Symbol symbol)
    {
        Symbol[] newSymbols = new Symbol[symbols.length + 1];

        for(int i = 0; i < symbols.length; i++)
        {
            newSymbols[i] = symbols[i];
        }

        newSymbols[symbols.length] = symbol;
        symbols = newSymbols;

        updatePositions();
    }

    public void delSymbol(String color)
    {
        for(int i = 0; i < symbols.length; i++)
        {
            if(symbols[i].getColor().equals(color))
            {
                Symbol[] newSymbols = new Symbol[symbols.length - 1];

                for(int j = 0; j < i; j++)
                {
                    newSymbols[j] = symbols[j];
                }

                for(int j = i; j < newSymbols.length; j++)
                {
                    newSymbols[j] = symbols[j + 1];
                }

                symbols = newSymbols;

                if(currentPosition >= symbols.length)
                {
                    currentPosition = 0;
                }

                updatePositions();
                return;
            }
        }
    }

    public void spin(int steps)
    {
        if(symbols.length == 0)
        {
            return;
        }

        currentPosition = (currentPosition + steps) % symbols.length;

        if(currentPosition < 0)
        {
            currentPosition += symbols.length;
        }
    }

    public Symbol getCurrentSymbol()
    {
        if(symbols.length == 0)
        {
            return null;
        }

        return symbols[currentPosition];
    }

    public Symbol[] getSymbols()
    {
        return symbols;
    }

    public void makeVisible()
    {
        for(int i = 0; i < symbols.length; i++)
        {
            symbols[i].makeVisible();
        }
    }

    public void makeInvisible()
    {
        for(int i = 0; i < symbols.length; i++)
        {
            symbols[i].makeInvisible();
        }
    }

    private void updatePositions()
    {
        for(int i = 0; i < symbols.length; i++)
        {
            int x = xPosition;
            int y = yPosition + i * 40;

            symbols[i].setPosition(x, y);
        }
    }
}




