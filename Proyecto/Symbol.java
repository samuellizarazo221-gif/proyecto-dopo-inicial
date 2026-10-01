
/**
 * Representa un símbolo (identificado por un color) de la máquina
 * tragamonedas. Se dibuja como un Circle de ese color.
 */
public class Symbol
{
    private String color;
    private Circle circle;
    public static final int DIAMETER = 30;
    
    /**
     * Crea un símbolo del color indicado, ubicado inicialmente en la
     * posición horizontal pos. En la práctica, la rueda que recibe
     * este símbolo (ver Wheel.addSymbol) lo reubica de inmediato con
     * su propia posición, así que el valor de pos aquí casi nunca
     * importa fuera de ese contexto.
     */
    public Symbol(String color)
    {
        this.color = color;
        circle = new Circle();
        circle.changeColor(color);
    }

    /**
     * Color de este símbolo.
     */
    public String getColor()
    {
        return color;
    }
    
    /**
     * Desplaza el símbolo distance píxeles horizontalmente desde su
     * posición actual (a diferencia de setPosition, que reposiciona a
     * una coordenada absoluta, este es un movimiento relativo).
     */
    public void moveHorizontal(int distance)
    {
        circle.moveHorizontal(distance);
    }
    
    /**
     * Reubica el símbolo en la posición horizontal pos (misma
     * convención de píxeles que usan Wheel y Rectangle), a una
     * altura fija de 50.
     */
    public void setPosition(int x, int y){
        circle.moveHorizontal(x - 20);
        circle.moveVertical(y - 15);
    }

    /**
     * Hace visible el símbolo.
     */
    public void makeVisible()
    {
        circle.makeVisible();
    }

    /**
     * Oculta el símbolo.
     */
    public void makeInvisible()
    {
        circle.makeInvisible();
    }

    /**
     * Cuenta cuántos colores distintos (ignorando nulos) hay en el
     * arreglo recibido. Utilidad estática usada por
     * SlotMachine.distinctSymbols().
     */
    public static int getDistinctSymbols(String[] colors)
    {
        int distinct = 0;

        for (int i = 0; i < colors.length; i++)
        {
            if (colors[i] == null)
            {
                continue;
            }

            boolean repeated = false;

            for (int j = 0; j < i; j++)
            {
                if (colors[i].equals(colors[j]))
                {
                    repeated = true;
                    break;
                }
            }

            if (!repeated)
            {
                distinct++;
            }
        }

        return distinct;
    }
}



