
/**
 * Write a description of class slotmachine here.
 * 
 * @author (your name) 
 * @version (a version number or a date)
 */
/**
 * Representa una máquina tragamonedas.
 */
/**
 * Representa una máquina tragamonedas.
 */
/**
 * Representa una máquina tragamonedas.
 */
/**
 * Representa una máquina tragamonedas.
 */
/**
 * Representa una máquina tragamonedas.
 */
import javax.swing.JOptionPane;

public class SlotMachine
{
    private boolean visible;
    private Wheel[] wheels;

    public SlotMachine()
    {
        visible = true;
        wheels = new Wheel[0];
    }

    public void addWheel(int pos)
    {
        if(pos < 0 || pos > wheels.length)
        {
            error("Posición de rueda inválida.");
            return;
        }

        Wheel[] newWheels = new Wheel[wheels.length + 1];

        for(int i = 0; i < pos; i++)
        {
            newWheels[i] = wheels[i];
        }

        newWheels[pos] = new Wheel(50 + pos * 70, 50);

        for(int i = pos; i < wheels.length; i++)
        {
            newWheels[i + 1] = wheels[i];
        }

        wheels = newWheels;

        if(visible)
        {
            wheels[pos].makeVisible();
        }
    }

    public void delWheel(int pos)
    {
        if(pos < 0 || pos >= wheels.length)
        {
            error("No existe esa rueda.");
            return;
        }

        wheels[pos].makeInvisible();

        Wheel[] newWheels = new Wheel[wheels.length - 1];

        for(int i = 0; i < pos; i++)
        {
            newWheels[i] = wheels[i];
        }

        for(int i = pos; i < newWheels.length; i++)
        {
            newWheels[i] = wheels[i + 1];
        }

        wheels = newWheels;
    }

    public void addSymbol(int pos, String color)
    {
        if(pos < 0 || pos >= wheels.length)
        {
            error("No existe esa rueda.");
            return;
        }

        wheels[pos].addSymbol(new Symbol(color));

        if(visible)
        {
            wheels[pos].makeVisible();
        }
    }

    public void delSymbol(int pos, String color)
    {
        if(pos < 0 || pos >= wheels.length)
        {
            error("No existe esa rueda.");
            return;
        }

        wheels[pos].delSymbol(color);
    }

    public void spin(int wheel, int steps)
    {
        if(wheel < 0 || wheel >= wheels.length)
        {
            error("No existe esa rueda.");
            return;
        }

        wheels[wheel].spin(steps);
    }

    public void spin()
    {
        for(int i = 0; i < wheels.length; i++)
        {
            wheels[i].spin(1);
        }
    }

    private String[] symbols()
    {
        String[] result = new String[wheels.length];

        for(int i = 0; i < wheels.length; i++)
        {
            Symbol symbol = wheels[i].getCurrentSymbol();

            if(symbol != null)
            {
                result[i] = symbol.getColor();
            }
        }

        return result;
    }

    public int distinctSymbols()
    {
        String[] currentSymbols = symbols();
        int different = 0;

        for(int i = 0; i < currentSymbols.length; i++)
        {
            if(currentSymbols[i] == null)
            {
                continue;
            }

            boolean repeated = false;

            for(int j = 0; j < i; j++)
            {
                if(currentSymbols[i].equals(currentSymbols[j]))
                {
                    repeated = true;
                    break;
                }
            }

            if(!repeated)
            {
                different++;
            }
        }

        return different;
    }

    public boolean isWinner()
    {
        if(wheels.length == 0)
        {
            return false;
        }

        String[] currentSymbols = symbols();

        if(currentSymbols[0] == null)
        {
            return false;
        }

        for(int i = 1; i < currentSymbols.length; i++)
        {
            if(currentSymbols[i] == null)
            {
                return false;
            }

            if(!currentSymbols[0].equals(currentSymbols[i]))
            {
                return false;
            }
        }

        return true;
    }

    public void showWinner()
    {
        if(isWinner())
        {
            JOptionPane.showMessageDialog(
                null,
                "¡JACKPOT! Todos los símbolos son iguales."
            );
        }
    }

    public void makeVisible()
    {
        visible = true;

        for(int i = 0; i < wheels.length; i++)
        {
            wheels[i].makeVisible();
        }
    }

    public void makeInvisible()
    {
        visible = false;

        for(int i = 0; i < wheels.length; i++)
        {
            wheels[i].makeInvisible();
        }
    }

    public void exit()
    {
        System.exit(0);
    }

    private void error(String message)
    {
        if(visible)
        {
            JOptionPane.showMessageDialog(null, message);
        }
    }
}
